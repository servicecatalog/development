Custom Ant tasks required for the IBPM build. As these tasks are build with the
build system itself, a compiled version is checked-in with the 
oscm-ibpm-build project. After modifications to these tasks the packaging
result oscm-ibpm-build-antextenstions.jar has to be copied to 

    /oscm-ibpm-build/javalib/oscm-ibpm-build-antextenstions.jar
    
to become effective. The custom tasks defined herein are:

  <dependencyresolver />

      Recursively resolves the dependencies of a given set of projects and
      creates a path object with "resolved.projects.path" containing the
      locations of the resolved projects. Task properties:
        
      workspacedir : location of the directory that contains all projects
      projects     : comma separated list of projects to build

	         
  <dependencyproperties />
  
      Creates a set of properties describing the dependencies of a single
      project. This task is called for each project. Task properties:
      
      workspacedir : location of the directory that contains all projects
	  projectdir   : location of the project in focus 
	  resultdir    : location of the result folder
      
      The following global property will be defined by the task:
      
      project.name : name of the current project
      
      For each declared dependency the following properties are defined:
      
      project.<dep-project>.dir         : project source location
	  result.work.<dep-project>.dir     : working location
	  result.package.<dep-project>.dir  : packaging output location
    
      I addition path objects with the following ids will be created:

      dependencies.compile.path         : all direct dependencies with exported
                                          libraries only
      dependencies.runtime.path         : all recursive dependencies including
                                          non-exported libraries

  <resourcepackage />
  
      Creates the output package of a single project according to the given
      specification. Task properties:
      
      packagefile : properties file with packaging specification
	  outputdir   : packaging output location
