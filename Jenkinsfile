BUILD_STATUS = 'success';
gitUri = 'https://scm.service.lixtec.fr/lixtec/pmeflow.git';
teamsHook  = '${TEAMS_BOT_URI}';
branchName = 'develop'

@NonCPS
def onFailed(e) {
    currentBuild.result = 'FAILED'
    BUILD_STATUS = 'FAILED'
    def msg = 'The '+JOB_NAME+' - Build # '+BUILD_NUMBER+' is '+BUILD_STATUS+'. \n Check console output at '+BUILD_URL+' to view the results ';   
    office365ConnectorSend message: msg, status: BUILD_STATUS, webhookUrl: teamsHook, color: "dd4040"
    
}

withCredentials([usernamePassword(credentialsId: 'e1529c62-f3ec-4b12-bbad-2a352fda9af2', usernameVariable: 'JENKINS_LOGIN', passwordVariable: 'JENKINS_PWD')]) {
	node('slave-gradle-jdk11') {    
	    stage('Init'){
	        echo 'Initialisation started'
	        office365ConnectorSend message: 'The '+JOB_NAME+' - Build # '+BUILD_NUMBER+'  start. \n Check console output at '+BUILD_URL+' to view the results ' , webhookUrl: teamsHook, color: "rgb(184, 255, 184)"
	        
	        try{
	            sh 'export SOURCE_BUILD_NUMBER=${BUILD_NUMBER} && ${WORKSPACE}/gradlew --stop -Djenkins.login=${JENKINS_LOGIN} -Djenkins.password=${JENKINS_PWD} '
	        }
	        catch (e){
	        }
	        try
	        {
	            git credentialsId: 'e1529c62-f3ec-4b12-bbad-2a352fda9af2', url: gitUri, branch: branchName
	            sh 'chmod -R 755 ${WORKSPACE}'
	            sh 'export SOURCE_BUILD_NUMBER=${BUILD_NUMBER} && ${WORKSPACE}/gradlew  -Djenkins.login=${JENKINS_LOGIN} -Djenkins.password=${JENKINS_PWD} clean --stacktrace'            
	        }
	        catch (e){
	            onFailed(e);
	            error e
	        }
	        echo 'Initialisation finished'
	    }
	    
	    stage('Check'){
	        echo 'Check started'
	        try{
	            sh 'export SOURCE_BUILD_NUMBER=${BUILD_NUMBER} && ${WORKSPACE}/gradlew  -Djenkins.login=${JENKINS_LOGIN} -Djenkins.password=${JENKINS_PWD}  check --stacktrace'         
	        }
	        catch (e){
	            onFailed(e);
	            error e
	        }
	        echo 'Check finished'
	    }
	    
	    stage('Distribution for test'){
	        echo 'Distribution for test started'
	        retry(2){ 
	            try{
                    sh 'export SOURCE_BUILD_NUMBER=${BUILD_NUMBER} && ${WORKSPACE}/gradlew -Djenkins.login=${JENKINS_LOGIN} -Djenkins.password=${JENKINS_PWD}  -Dorg.ajoberstar.grgit.auth.username=${JENKINS_LOGIN} -Dorg.ajoberstar.grgit.auth.password=${JENKINS_PWD} --stacktrace publish -Psigning.password=${KS_PWD}'
	            }
	            catch (e){
	                onFailed(e);
	                error e
	            }
	        }
	        echo 'Distribution for test finished'
	    }
	       
	    stage('Code review & report'){
	        echo 'Code review & report started'
	        try{
	            sh 'export SOURCE_BUILD_NUMBER=${BUILD_NUMBER} && ${WORKSPACE}/gradlew  -Djenkins.login=${JENKINS_LOGIN} -Djenkins.password=${JENKINS_PWD}  -x test --stacktrace sonarqube checkSonarQualityGate'
	        }
	        catch (e){
	            onFailed(e);
	            error e
	        }
	        echo 'Code review & report finished'
	    }
            
        stage('Expose docker solution'){
            echo 'Expose docker solution started'
            retry(2){ 
                try{
                    withCredentials([usernamePassword(credentialsId: 'DOCKERHUB', usernameVariable: 'DOCKER_LOGIN', passwordVariable: 'DOCKER_PWD')]) {
                        sh 'export SOURCE_BUILD_NUMBER=${BUILD_NUMBER} && ${WORKSPACE}/gradlew -Pdockerhub_username=${DOCKER_LOGIN} -Pdockerhub_password=${DOCKER_PWD} --stacktrace buildAndPushDockerImages'
                    }
                }
                catch (e){
                    onFailed(e);
                    error e
                }
            }
            echo 'Expose docker solution finished'
        }
	    
	    stage('Distribution for production'){
	        echo 'Distribution for production started'
	        retry(2){ 
	            try{
	            	sh 'export SOURCE_BUILD_NUMBER=${BUILD_NUMBER} && ${WORKSPACE}/gradlew -Djenkins.login=${JENKINS_LOGIN} -Djenkins.password=${JENKINS_PWD}  -Dorg.ajoberstar.grgit.auth.username=${JENKINS_LOGIN} -Dorg.ajoberstar.grgit.auth.password=${JENKINS_PWD} --stacktrace publish -Psigning.password=${KS_PWD} -Penv=prod' 
	                if(!currentBuild.result)
	                {
	                   currentBuild.result = 'SUCCESS'
	                   BUILD_STATUS = 'SUCCESS'
	                }
	                def msg = 'The '+JOB_NAME+' - Build # '+BUILD_NUMBER+' is '+BUILD_STATUS+'. \n Check console output at '+BUILD_URL+' to view the results.';
    				office365ConnectorSend message: msg, status: BUILD_STATUS, webhookUrl: teamsHook, color: "rgb(184, 255, 184)"
	            }
	            catch (e){
	                onFailed(e);
	                error e
	            }
	        }
	        echo 'Distribution for production finished'
	    }
	}
}