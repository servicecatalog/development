package org.oscm.example.servlets;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import org.oscm.example.client.BssClient;
import org.oscm.example.client.ObjectNotFoundExceptionException;
import org.oscm.example.client.OrganizationAuthoritiesExceptionException;
import org.oscm.example.client.ValidationExceptionException;
import org.oscm.example.common.Constants;
import org.oscm.example.common.InetLookup;
import org.oscm.example.common.ServiceParameterDAO;
import org.oscm.example.server.ProvisioningServiceSkeleton;

public class ExampleServlet extends HttpServlet {

    private static final long serialVersionUID = 1369077840252614627L;
    private static final String ERROR_ACCESS = "Access denied: ";
    private static final String ERROR_COPY = "Cannot copy: ";
    private static final String ERROR_CREATE = "Cannot create Folder: ";
    private static final String ERROR_DELETE = "Cannot delete: ";
    private static final String ERROR_TARGET = "The target must be a directory.";
    private static final String ERROR_TARGET_MISSING = "Name of target directory is missing.";
    private static final String ERROR_UPLOAD = "Upload Error.";
    private static final String ERROR_PARAM_FILE = "Maximum number of files reached.";
    private static final String ERROR_PARAM_FOLDER = "Maximum number of folders reached.";

    private static final Map<String, String> contentTypeMap = Collections
            .synchronizedMap(new HashMap<String, String>());

    private static final long MULTIPLIER = 10;

    static {
        contentTypeMap.put("doc", "application/msword");
        contentTypeMap.put("dot", "application/msword");
        contentTypeMap.put("doc", "application/msword");
        contentTypeMap.put("doc", "application/msword");

        contentTypeMap.put("ppt", "application/vnd.ms-powerpoint");
        contentTypeMap.put("pps", "application/vnd.ms-powerpoint");

        contentTypeMap.put("xls", "application/vnd.ms-excel");

        contentTypeMap.put("pdf", "application/pdf");

        contentTypeMap.put("htm", "text/html");
        contentTypeMap.put("html", "text/html");

        contentTypeMap.put("css", "text/css");
        contentTypeMap.put("txt", "text/plain");
        contentTypeMap.put("text", "text/plain");
        contentTypeMap.put("conf", "text/plain");
        contentTypeMap.put("cfg", "text/plain");
        contentTypeMap.put("log", "text/plain");

        contentTypeMap.put("gif", "image/gif");
        contentTypeMap.put("jpeg", "image/jpeg");
        contentTypeMap.put("jpg", "image/jpeg");
        contentTypeMap.put("jpe", "image/jpeg");
        contentTypeMap.put("png", "image/png");
        contentTypeMap.put("bmp", "image/bmp");
    }

    public static int getFileCount(File dir) {
        int n = 0;
        String[] elementArray = dir.list();
        for (int i = 0; i < elementArray.length; i++) {
            File file = new File(dir, elementArray[i]);
            if (file.isDirectory()) {
                n += getFileCount(file);
            } else {
                n++;
            }
        }
        return n;
    }

    public static int getFolderCount(File dir) {
        int n = 0;
        String[] elementArray = dir.list();
        if (elementArray != null) {
            for (int i = 0; i < elementArray.length; i++) {
                File file = new File(dir, elementArray[i]);
                if (file.isDirectory()) {
                    n += getFolderCount(file);
                    n++;
                }
            }
        }
        return n;
    }

    private int getIntProp(File rootDir, String name) {
        ServiceParameterDAO dao = new ServiceParameterDAO();
        try {
            Properties prop = dao.load(rootDir);
            if (prop != null) {
                String val = prop.getProperty(name);
                if (val != null) {
                    return Integer.parseInt(val);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }

    private boolean isUploadPossible(File instanceRootPath) {
        int maxFiles = getIntProp(instanceRootPath,
                ProvisioningServiceSkeleton.PARAM_MAX_FILE_NUM);
        if (maxFiles >= 0) {
            if (maxFiles <= getFileCount(instanceRootPath)) {
                return false;
            }
        }
        return true;
    }

    private boolean isCreatePossible(File instanceRootPath) {
        int maxFolders = getIntProp(instanceRootPath,
                ProvisioningServiceSkeleton.PARAM_MAX_FOLDER_NUM);
        if (maxFolders >= 0) {
            if (maxFolders <= getFolderCount(instanceRootPath)) {
                return false;
            }
        }
        return true;
    }

    public static String getRealRootPath(HttpServletRequest request)
            throws IOException {
        String remoteHostName = request.getRemoteHost();
        if ("127.0.0.1".equals(remoteHostName)) {
            remoteHostName = "localhost";
        } else {
            remoteHostName = InetLookup.resolveHost(remoteHostName);
            int idx = remoteHostName.indexOf('.');
            if (idx > 0) {
                remoteHostName = remoteHostName.substring(0, idx);
            }
        }
        return new File(Constants.ROOT_PATH, remoteHostName).getCanonicalPath();
    }

    private String getContentType(File file) {
        String name = file.getName();
        int idx = name.lastIndexOf('.');
        if (idx >= 0) {
            String ext = name.substring(idx + 1);
            String contentType = contentTypeMap.get(ext);
            if (contentType != null) {
                return contentType;
            }
        }

        return "application/octet-stream";
    }

    private String getSaasId(HttpServletRequest request) {
        return (String) request.getSession().getAttribute(Constants.SAAS_ID);
    }

    private String getUserId(HttpServletRequest request) {
        return (String) request.getSession().getAttribute(Constants.USER_ID);
    }

    /**
     * Copy a file
     * 
     * @param source
     *            - the source file
     * @param target
     *            - the target file
     * @throws IOException
     *             - if an input or output error is detected during the file
     *             acccess
     */
    private void copy(File source, File target) throws IOException {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(source);
            fos = new FileOutputStream(target);
            byte[] buf = new byte[1024];
            int i = 0;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }

    /**
     * Copy the elements from the given directory into another directory.
     * 
     * @param request
     *            - the HttpServletRequest object
     * @param dir
     *            - the directory which contains the source files
     * @param name
     *            - the name of the target directory
     * @param elementList
     *            - the names of the elements (files/directories) to delete
     */
    private void copy(HttpServletRequest request, File dir, String name,
            List<String> elementList) {
        if (elementList == null) {
            return;
        }
        if (name == null || name.length() == 0) {
            addError(request, ERROR_TARGET_MISSING);
            return;
        }
        File targetDir = new File(dir, name);
        if (!targetDir.isDirectory()) {
            addError(request, ERROR_TARGET);
            return;
        }
        if (isAccessible(request, targetDir)) {
            for (Iterator<String> it = elementList.iterator(); it.hasNext();) {
                String element = it.next();
                if (element.length() > 0) {
                    try {
                        File source = new File(dir, element);
                        File target = new File(targetDir, element);
                        if (source.isDirectory()) {
                            target.mkdirs();
                            String targetName = name;
                            if (!targetName.endsWith("/")
                                    && !targetName.endsWith("\\")) {
                                targetName += "/";
                            }
                            targetName += element;
                            copy(request, source, "../" + targetName,
                                    Arrays.asList(source.list()));
                        } else {
                            copy(source, target);
                        }
                    } catch (IOException e) {
                        addError(request, ERROR_COPY + element);
                    }
                }
            }
        }
    }

    /**
     * Create a new sub directory in the given directory.
     * 
     * @param request
     *            - the HttpServletRequest object
     * @param dir
     *            - the directory in which the new sub directory is created
     * @param name
     *            - the name of the new sub directory
     * @throws IOException
     *             - if an input or output error is detected when the servlet
     *             handles the request
     * @throws ServletException
     */
    private void create(HttpServletRequest request, File dir, String name)
            throws IOException, ServletException {
        if (name == null || name.length() == 0) {
            return;
        }
        File newDir = new File(dir, name);
        if (isAccessible(request, newDir)) {
            if (newDir.mkdirs()) {
                BssClient bssClient = new BssClient(request.getRemoteHost());
                try {
                    bssClient.recordEvent(getSaasId(request),
                            ProvisioningServiceSkeleton.EVENT_ID_FOLDER_NEW,
                            getUserId(request), MULTIPLIER);
                } catch (OrganizationAuthoritiesExceptionException e) {
                    addError(request, e.getMessage());
                } catch (ValidationExceptionException e) {
                    addError(request, e.getMessage());
                } catch (ObjectNotFoundExceptionException e) {
                    addError(request, e.getMessage());
                }
            } else {
                addError(request, ERROR_CREATE + name);
            }
        }
    }

    /**
     * Delete the requested files/directories from the given directory.
     * 
     * @param request
     *            - the HttpServletRequest object
     * @param dir
     *            - the directory from which the files/directories are deleted
     * @param elementList
     *            - the names of the elements (files/directories) to delete
     * @throws IOException
     *             - if an input or output error is detected when the servlet
     *             handles the request
     */
    private void delete(HttpServletRequest request, File dir,
            List<String> elementList) throws IOException {
        if (elementList == null) {
            return;
        }
        for (Iterator<String> it = elementList.iterator(); it.hasNext();) {
            String element = it.next();
            if (element.length() > 0) {
                File file = new File(dir, element);
                if (isAccessible(request, file)) {
                    if (file.isDirectory()) {
                        delete(request, file, Arrays.asList(file.list()));
                    }
                    if (!file.delete()) {
                        addError(request, ERROR_DELETE + element);
                    }
                }
            }
        }
    }

    /**
     * Rename the requested element (file/directory) in the given directory.
     * 
     * @param request
     *            - the HttpServletRequest object
     * @param dir
     *            - the directory in which the element is renamed
     * @param element
     *            - the names of the elements (files/directories) to rename
     * @param newName
     *            - the new name of the element
     */
    private void rename(HttpServletRequest request, File dir, String element,
            String newName) {
        if (element != null && element.length() > 0) {
            File src = new File(dir, element);
            File dest = new File(dir, newName);
            if (isAccessible(request, src) && isAccessible(request, dest)) {
                src.renameTo(dest);
            }
        }
    }

    /**
     * Upload a file into the given directory.
     * 
     * @param request
     *            - the HttpServletRequest object
     * @param response
     *            - the HttpServletResponse object
     * @param dir
     *            - the directory in which the files/directories are deleted
     * @param fileItemList
     *            - the list with the uploaded files
     */
    private void upload(HttpServletRequest request, File dir,
            List<FileItem> fileItemList) {

        if (fileItemList == null) {
            return;
        }

        // Process the uploaded items
        Iterator<FileItem> it = fileItemList.iterator();
        while (it.hasNext()) {
            FileItem item = it.next();

            // Process a file upload
            if (!item.isFormField()) {
                String name = item.getName();
                int i = name.lastIndexOf("/");
                if (i < 0) {
                    i = name.lastIndexOf("\\");
                }
                if (i >= 0) {
                    name = name.substring(i + 1);
                }
                File file = new File(dir, name);
                if (isAccessible(request, file)) {
                    try {
                        item.write(file);
                        BssClient bssClient = new BssClient(
                                request.getRemoteHost());
                        bssClient
                                .recordEvent(
                                        getSaasId(request),
                                        ProvisioningServiceSkeleton.EVENT_ID_FILE_UPLOAD,
                                        getUserId(request), MULTIPLIER);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    /**
     * ZIP the requested elements (files/directories) in the given directory and
     * download the resulting ZIP file.
     * 
     * @param request
     *            - the HttpServletRequest object
     * @param dir
     *            - the directory in which the element is renamed
     * @param elementList
     *            - the names of the elements (files/directories) to zip
     * @throws IOException
     *             - if an input or output error is detected when the servlet
     *             handles the request
     */
    private ByteArrayOutputStream zip(HttpServletRequest request, File dir,
            List<String> elementList) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        for (Iterator<String> it = elementList.iterator(); it.hasNext();) {
            String element = it.next();
            if (element.length() == 0) {
                continue;
            }
            File file = new File(dir, element);
            if (isAccessible(request, file)) {
                if (file.isDirectory()) {
                    zipDir(zos, element, file);
                } else {
                    zipFile(zos, element, file);
                }
            }
        }
        zos.close();

        return baos;
    }

    /**
     * Add a directory (inclusive sub directories) to the ZIP stream.
     * 
     * @param zos
     *            - the ZIP output stream
     * @param pathInZip
     *            - the path of the directory in the ZIP
     * @param dir
     *            - the directory which is added
     * @throws IOException
     *             - if an IO error is detected
     */
    private void zipDir(ZipOutputStream zos, String pathInZip, File dir)
            throws IOException {
        String[] elementArray = dir.list();
        for (int i = 0; i < elementArray.length; i++) {
            File file = new File(dir, elementArray[i]);

            if (file.isDirectory()) {
                zipDir(zos, pathInZip + File.separator + elementArray[i], file);
            } else {
                zipFile(zos, pathInZip + File.separator + elementArray[i], file);
            }

        }
    }

    /**
     * Add a file to the ZIP stream.
     * 
     * @param zos
     *            - the ZIP output stream
     * @param pathInZip
     *            - the path of the directory in the ZIP
     * @param file
     *            - the file which is added
     * @throws IOException
     *             - if an IO error is detected
     */
    private void zipFile(ZipOutputStream zos, String pathInZip, File file)
            throws IOException {
        byte[] buf = new byte[1024];

        zos.putNextEntry(new ZipEntry(pathInZip));

        FileInputStream in = null;
        try {
            in = new FileInputStream(file.getCanonicalFile());
            int len;
            while ((len = in.read(buf)) > 0) {
                zos.write(buf, 0, len);
            }
        } finally {
            if (in != null)
                in.close();
            zos.closeEntry();
        }

    }

    /**
     * Return true if and only if the user is allowed to access the given file.
     * 
     * @param request
     *            - the HttpServletRequest object
     * @param file
     *            - the file to check
     * @return true if and only if the user is allowed to access the given file.
     */
    private boolean isAccessible(HttpServletRequest request, File file) {
        try {
            if (file.getCanonicalPath().startsWith(getRealRootPath(request))) {
                return true;
            }
        } catch (IOException e) {
        }
        addError(request, ERROR_ACCESS + file);
        return false;
    }

    /**
     * Move the elements from the given directory into another directory.
     * 
     * @param request
     *            - the HttpServletRequest object
     * @param dir
     *            - the directory which contains the source files
     * @param name
     *            - the name of the target directory
     * @param elementList
     *            - the names of the elements (files/directories) to delete
     */
    private void move(HttpServletRequest request, File dir, String name,
            List<String> elementList) {
        if (elementList == null) {
            return;
        }
        if (name == null || name.length() == 0) {
            addError(request, ERROR_TARGET_MISSING);
            return;
        }
        File destDir = new File(dir, name);
        if (!destDir.isDirectory()) {
            addError(request, ERROR_TARGET);
            return;
        }
        if (isAccessible(request, destDir)) {
            for (Iterator<String> it = elementList.iterator(); it.hasNext();) {
                String element = it.next();
                if (element.length() > 0) {
                    File src = new File(dir, element);
                    File dest = new File(destDir, element);
                    src.renameTo(dest);
                }
            }
        }
    }

    /**
     * Process a GET or POST request.
     * 
     * @param request
     *            - the HttpServletRequest object
     * @param response
     *            - the HttpServletResponse object
     * @throws IOException
     *             - if an input or output error is detected when the servlet
     *             handles the request
     * @throws ServletException
     *             - if the request could not be handled
     */
    @SuppressWarnings("unchecked")
    protected void process(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        String method = null;
        String name = "";
        String path = "";
        List<String> elementList = new ArrayList<String>();
        List<FileItem> fileItemList = null;
        String saasId = "";
        String userId = "";
        String instanceId = "";
        String contextPath = null;

        HttpSession session = request.getSession();
        method = request.getParameter(Constants.PARAM_METHOD);
        if (method != null && method.equals(Constants.LOGOUT)) {
            BssClient bssClient = new BssClient(request.getRemoteHost());
            String logoutPath = bssClient.logoutUser((String) session
                    .getAttribute(Constants.SAAS_ID));
            response.sendRedirect(logoutPath);
            session.invalidate();
            return;
        }

        if (method != null && method.equals(Constants.METHOD_RESET)) {
            // reset especially for component test
            File dir = new File(getRealRootPath(request));
            String[] names = dir.list();
            if (names != null) {
                delete(request, dir, Arrays.asList(names));
                response.getWriter().println(
                        "<html><body>Content of directory " + dir
                                + " deleted!</body></html>");
            }
            return;
        }

        userId = (String) session.getAttribute(Constants.USER_ID);
        saasId = (String) session.getAttribute(Constants.SAAS_ID);
        instanceId = (String) session.getAttribute(Constants.INSTANCE_ID);
        boolean flag = false;
        if (flag) {
            // test without platform
            userId = "admin";
            saasId = "saasId";
            instanceId = "instanceId";
            File dir = new File(getRealRootPath(request), instanceId);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }

        if (userId == null || userId.length() == 0 || saasId == null
                || saasId.length() == 0 || instanceId == null
                || instanceId.length() == 0) {
            String usertoken = request.getParameter(Constants.USERTOKEN);
            saasId = request.getParameter(Constants.SAAS_ID);
            instanceId = request.getParameter(Constants.INSTANCE_ID);
            contextPath = request.getParameter(Constants.CONTEXT_PATH);

            BssClient bssClient = new BssClient(request.getRemoteHost());
            if (usertoken != null && saasId != null) {
                userId = bssClient.resolveUsertoken(usertoken, saasId);
                session.setAttribute(Constants.USER_ID, userId);
                session.setAttribute(Constants.SAAS_ID, saasId);
                session.setAttribute(Constants.INSTANCE_ID, instanceId);
            }
        }

        if (userId == null || userId.length() == 0 || saasId == null
                || saasId.length() == 0 || instanceId == null
                || instanceId.length() == 0) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (contextPath != null) {
            Properties props = BssClient.getProperties();
            String baseUrl = props.getProperty("mybaseUrl");
            if (baseUrl == null) {
                response.sendRedirect(request.getContextPath() + contextPath);
            } else {
                response.sendRedirect(baseUrl + contextPath);
            }
            return;
        }

        // Check that we have a file upload request
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);

        if (isMultipart) {
            // Create a factory for disk-based file items
            DiskFileItemFactory factory = new DiskFileItemFactory();

            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload(factory);

            // Parse the request
            try {
                fileItemList = upload.parseRequest(request);
                Iterator<FileItem> it = fileItemList.iterator();
                while (it.hasNext()) {
                    FileItem item = it.next();
                    // Process the form fields
                    if (item.isFormField()) {
                        if (Constants.PARAM_METHOD.equals(item.getFieldName())) {
                            method = item.getString();
                        } else if (Constants.PARAM_NAME.equals(item
                                .getFieldName())) {
                            name = item.getString();
                        } else if (Constants.PARAM_PATH.equals(item
                                .getFieldName())) {
                            path = item.getString();
                        } else if (Constants.PARAM_ELEMENT.equals(item
                                .getFieldName())) {
                            elementList.add(item.getString());
                        }
                    }
                }
            } catch (FileUploadException e1) {
                addError(request, ERROR_UPLOAD);
                e1.printStackTrace();
            }
        } else {
            method = request.getParameter(Constants.PARAM_METHOD);
            name = request.getParameter(Constants.PARAM_NAME);
            path = request.getParameter(Constants.PARAM_PATH);
            String[] elementArray = request
                    .getParameterValues(Constants.PARAM_ELEMENT);
            if (elementArray != null) {
                Collections.addAll(elementList,
                        request.getParameterValues(Constants.PARAM_ELEMENT));
            }
        }
        request.setAttribute(Constants.PARAM_NAME, name);

        File instanceRootPath = new File(getRealRootPath(request), instanceId);
        if (!instanceRootPath.exists()) {
            // the instance must have been created before the service can be
            // used
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (path == null) {
            path = "";
        }
        while (path.endsWith("/") || path.endsWith("\\")) {
            path = path.substring(0, path.length() - 1);
        }
        File dir = new File(instanceRootPath, path);
        if (!isAccessible(request, dir)) {
            path = "";
            dir = instanceRootPath;
        } else if (dir.getCanonicalPath().equals(
                instanceRootPath.getCanonicalPath())) {
            path = "";
        } else {
            String parentPath = path;
            while (parentPath.length() > 0 && !parentPath.endsWith("/")
                    && !parentPath.endsWith("\\")) {
                parentPath = parentPath.substring(0, parentPath.length() - 1);
            }
            if (parentPath.length() > 0) {
                parentPath = parentPath.substring(0, parentPath.length() - 1);
            }
            request.setAttribute(Constants.ATTRIBUTE_PARENT_PATH,
                    URLEncoder.encode(parentPath, Constants.URL_ENCODING));
        }

        if (Constants.METHOD_COPY.equals(method)) {
            copy(request, dir, name, elementList);
        } else if (Constants.METHOD_CREATE.equals(method)) {
            if (isCreatePossible(instanceRootPath)) {
                create(request, dir, name);
            } else {
                addError(request, ERROR_PARAM_FOLDER);
            }
        } else if (Constants.METHOD_DELETE.equals(method)) {
            delete(request, dir, elementList);
        } else if (Constants.METHOD_MOVE.equals(method)) {
            move(request, dir, name, elementList);
        } else if (Constants.METHOD_RENAME.equals(method)) {
            if (elementList.size() == 1) {
                rename(request, dir, elementList.get(0), name);
            }
        } else if (Constants.METHOD_UPLOAD.equals(method)) {
            if (isUploadPossible(instanceRootPath)) {
                upload(request, dir, fileItemList);
            } else {
                addError(request, ERROR_PARAM_FILE);
            }
        } else if (Constants.METHOD_ZIP.equals(method)
                && elementList.size() == 1) {
            ByteArrayOutputStream baos = zip(request, dir, elementList);
            response.setContentType("application/zip");
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.format(cal.getTime());
            response.setHeader("Content-Disposition", "attachment;filename=\""
                    + sdf.format(cal.getTime()) + "-mmp.zip\"");
            baos.writeTo(response.getOutputStream());
            return;
        } else if (Constants.METHOD_GET.equals(method)) {
            BssClient bssClient = new BssClient(request.getRemoteHost());
            try {
                bssClient.recordEvent(saasId,
                        ProvisioningServiceSkeleton.EVENT_ID_FILE_DOWNLOAD,
                        getUserId(request), MULTIPLIER);
            } catch (OrganizationAuthoritiesExceptionException e) {
                addError(request, e.getMessage());
            } catch (ValidationExceptionException e) {
                addError(request, e.getMessage());
            } catch (ObjectNotFoundExceptionException e) {
                addError(request, e.getMessage());
            }
            String element = elementList.get(0);
            File file = new File(dir, element);
            response.setContentType(getContentType(file));
            response.setHeader("Content-Disposition", "attachment;filename=\""
                    + elementList + "\"");
            FileInputStream in = null;
            ServletOutputStream out = null;
            byte[] buf = new byte[1024];
            int len;
            try {
                in = new FileInputStream(file.getCanonicalFile());
                out = response.getOutputStream();
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            }
            return;
        }

        request.setAttribute(Constants.ATTRIBUTE_PATH, path);

        File[] fileArray = dir.listFiles();
        request.setAttribute(Constants.ATTRIBUTE_DIR_ELEMENTS, fileArray);

        request.getRequestDispatcher(Constants.JSP_list).forward(request,
                response);

    }

    /**
     * Add an error message to the list of error message which are displayed to
     * the user.
     * 
     * @param request
     *            - the HttpServletRequest object
     * @param text
     *            - the error message
     */
    @SuppressWarnings("unchecked")
    private void addError(HttpServletRequest request, String text) {
        List<String> list = (List<String>) request
                .getAttribute(Constants.ERROR_LIST);
        if (list == null) {
            list = new ArrayList<String>();
        }
        list.add(text);
        request.setAttribute(Constants.ERROR_LIST, list);
    }

    /**
     * Called by the server (via the service method) to allow a servlet to
     * handle a GET request.
     * 
     * @param request
     *            - the HttpServletRequest object
     * @param response
     *            - the HttpServletResponse object
     * @throws IOException
     *             - if an input or output error is detected when the servlet
     *             handles the GET request
     * @throws ServletException
     *             - if the request for the GET could not be handled
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(request, response);
    }

    /**
     * Called by the server (via the service method) to allow a servlet to
     * handle a POST request.
     * 
     * @param request
     *            - the HttpServletRequest object
     * @param response
     *            - the HttpServletResponse object
     * @throws IOException
     *             - if an input or output error is detected when the servlet
     *             handles the POST request
     * @throws ServletException
     *             - if the request for the POST could not be handled
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(request, response);
    }

}
