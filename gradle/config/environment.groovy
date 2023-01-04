desc_project{
    type="app-docker"
    withDocker=true
    withQuarkus=true
    version{
        majorVersion=11
        mediumVersion=1
        minorVersion=0
    }
    artefact{
        group="fr.lixtec"
        project="pmeflow"
        projectKey="${group}:${project}"
        dockerImageKey="pmeflow"
    }
}

pic{
    channel="lixtec"
	git{
	    uri="https://scm.service.lixtec.fr/${channel}/pmeflow.git"
	}    	
    jenkins{
        uri="https://ci.service.lixtec.fr/view/pmeflow"
    }  
    sonar{
        uri="https://quality.service.lixtec.fr"
    }
    artifactory{
		uri="https://repos.service.lixtec.fr/artifactory"
    }
	mavencentral{
		uri="https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
	}
}

repository{
	artifactory{
	    release	="pmeflow-release"
	    snapshot ="pmeflow-snapshot"
	    libsRelease="libs-release"
	}
}