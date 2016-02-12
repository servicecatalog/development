/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.build.ant;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.tools.ant.BuildException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Reader for Eclipse project definitions in a given workspace. The
 * implementation assumes that all projects reside in the same workspace and
 * caches the description objects. The name of each project must correspond to
 * its folder name.
 * 
 * @author hoffmann
 */
public class EclipseProjectReader {

    private static final String FILE_PROJECT = ".project";
    private static final String FILE_CLASSPATH = ".classpath";

    private final File workspace;
    private final Map<File, IEclipseProject> cache;

    /**
     * Constructs a new reader for the given workspace directory.
     * 
     * @param workspace
     *            workspace directory
     */
    public EclipseProjectReader(File workspace) {
        this.workspace = workspace;
        this.cache = new HashMap<File, IEclipseProject>();
    }

    /**
     * Scans the workspace for projects, i.e. looks for folders containing a
     * <code>.project</code> file.
     * 
     * @return names of all projects in the workspace
     */
    public String[] getProjectNames() {
        File[] folders = workspace.listFiles(new FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() && new File(f, FILE_PROJECT).exists();
            }
        });
        String[] names = new String[folders.length];
        for (int i = 0; i < folders.length; i++) {
            names[i] = folders[i].getName();
        }
        Arrays.sort(names);
        return names;
    }

    /**
     * Get the definition of the project with the give name in this workspace.
     * 
     * @param project
     *            name name of the project
     * @return project definition
     */
    public IEclipseProject getDefinition(String projectname) {
        File location = new File(workspace, projectname);
        return getDefinition(location);
    }

    /**
     * Get the definition of the project at the given location.
     * 
     * @param location
     *            location of the project
     * @return project definition
     */
    public IEclipseProject getDefinition(File location) {
        IEclipseProject definition = cache.get(location);
        if (definition == null) {
            definition = readDefinition(location);
            cache.put(location, definition);
        }
        return definition;
    }

    private IEclipseProject readDefinition(File location) {
        ProjectImpl definition = new ProjectImpl(location);
        readProjectFile(definition);
        readClasspathFile(definition);
        return definition;
    }

    private void readProjectFile(ProjectImpl definition) {
        Document doc = parse(new File(definition.getLocation(), FILE_PROJECT));
        Node namenode = doc.getElementsByTagName("name").item(0);
        definition.name = namenode.getFirstChild().getNodeValue();
        NodeList projectnodes = doc.getElementsByTagName("project");
        for (int i = 0; i < projectnodes.getLength(); i++) {
            Node name = projectnodes.item(i).getFirstChild();
            definition.dependencies.add(name.getNodeValue());
        }
    }

    private void readClasspathFile(ProjectImpl definition) {
        File classpathfile = new File(definition.getLocation(), FILE_CLASSPATH);
        // May not exist for non Java projects
        if (!classpathfile.exists())
            return;
        Document doc = parse(classpathfile);
        NodeList entries = doc.getElementsByTagName("classpathentry");
        for (int i = 0; i < entries.getLength(); i++) {
            NamedNodeMap attrs = entries.item(i).getAttributes();
            String path = attrs.getNamedItem("path").getNodeValue();
            File location = getLocation(definition.getLocation(), path);
            String kind = attrs.getNamedItem("kind").getNodeValue();
            if (kind.equals("lib")) {
                definition.libraries.add(location);
                Node exattr = attrs.getNamedItem("exported");
                if (exattr != null && "true".equals(exattr.getNodeValue())) {
                    definition.exportedlibraries.add(location);
                }
            }
            if (kind.equals("src")) {
                if (path.startsWith("/")) {
                    // Java project reference
                    String ref = path.substring(1);
                    definition.dependencies.add(ref);
                    definition.javapathdependencies.add(ref);
                } else {
                    // local source folder
                    definition.javasourcefolders.add(location);
                }
            }
        }
    }

    /**
     * Calculates the absolute location for a path specified within the given
     * project. If the given path starts with / the location is relative to the
     * workspace, of not it is relative to the project directory.
     * 
     * @param projectname
     *            name of the project
     * @param path
     *            local classpath path
     * @return absolute location
     */
    private File getLocation(File projectpath, String path) {
        if (path.startsWith("/")) {
            return new File(workspace, path.substring(1));
        } else {
            return new File(projectpath, path);
        }
    }

    private Document parse(File file) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(file);
        } catch (ParserConfigurationException ex) {
            throw new BuildException(ex);
        } catch (SAXException ex) {
            throw new BuildException(ex);
        } catch (IOException ex) {
            throw new BuildException("Error while reading project definition, "
                    + "additional checkout might be required (" + file + ").",
                    ex);
        }
    }

    private class ProjectImpl implements IEclipseProject {

        private final File location;
        private String name;
        final List<String> dependencies = new ArrayList<String>();
        final List<String> javapathdependencies = new ArrayList<String>();
        final List<File> javasourcefolders = new ArrayList<File>();
        final List<File> libraries = new ArrayList<File>();
        final List<File> exportedlibraries = new ArrayList<File>();

        ProjectImpl(File location) {
            this.location = location;
        }

        public String getName() {
            return name;
        }

        public File getLocation() {
            return location;
        }

        private List<IEclipseProject> getProjects(List<String> names,
                IEclipseProject referent) {
            List<IEclipseProject> projects = new ArrayList<IEclipseProject>(
                    names.size());
            try {
                for (String name : names) {
                    projects.add(getDefinition(name));
                }
            } catch (BuildException be) {
                String msg = String.format(
                        "Error while reading dependencies of project %s: %s",
                        referent.getName(), be.getMessage());
                throw new BuildException(msg, be);
            }
            return projects;
        }

        public List<IEclipseProject> getDependencies() {
            return getProjects(dependencies, this);
        }

        public List<IEclipseProject> getJavaPathDependencies() {
            return getProjects(javapathdependencies, this);
        }

        public List<File> getLibraries() {
            return Collections.unmodifiableList(libraries);
        }

        public List<File> getExportedLibraries() {
            return Collections.unmodifiableList(exportedlibraries);
        }

        public List<File> getJavaSourceFolders() {
            return Collections.unmodifiableList(javasourcefolders);
        }

        public String toString() {
            return "Project[" + name + "]";
        }

        public boolean equals(Object obj) {
            if (obj instanceof IEclipseProject) {
                return name.equals(((IEclipseProject) obj).getName());
            } else {
                return false;
            }
        }

        public int hashCode() {
            return name.hashCode();
        }

    }

}
