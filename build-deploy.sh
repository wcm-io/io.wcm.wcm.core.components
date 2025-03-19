#!/bin/bash
# #%L
#  wcm.io
#  %%
#  Copyright (C) 2019 wcm.io
#  %%
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#  #L%

# Call with "help" parameter to display syntax information

# defaults
MAVEN_PROFILES="fast"
SLING_URL=""
SLING_USER=""
SLING_PASSWORD=""
JVM_ARGS=""

# display pause message only when script was executed via double-click on windows
if [[ $0 == *":\\"* ]]; then
  DISPLAY_PAUSE_MESSAGE=true
fi
BUILD=false
DEPLOY=false
HELP=false
DEFAULT_COMMANDS=true
DEPLOY_CORE_COMPONENTS=false

####

help_message_exit() {
  echo ""
  echo "  Syntax <parameters> <commands>"
  echo ""
  echo "  Parameters:"
  echo "    --maven.profiles=${MAVEN_PROFILES}       or -P${MAVEN_PROFILES}"
  echo "    --sling.url=${SLING_URL}                 or -Dsling.url=${SLING_URL}"
  echo "    --sling.user=${SLING_USER}               or -Dsling.user=${SLING_USER}"
  echo "    --sling.password=${SLING_PASSWORD}       or -Dsling.password=${SLING_PASSWORD}"
  echo "    --jvm.args=${JVM_ARGS}                   or -Djvm.args=${JVM_ARGS}"
  echo "    --deploy.core.components=true|false      or -Ddeploy.core.components=true|false"
  echo ""
  echo "  Commands:"
  echo "    build  - Clean and install maven project"
  echo "    deploy - Deploy packages to AEM instance"
  echo "    help   - display this help message"
  echo ""

  exit 0
}

parse_parameters() {

  for i in "$@"
  do
  case $i in
      --maven\.profiles=*|-P*)
      MAVEN_PROFILES="${i#*=}"
      shift # past argument=value
      ;;
      --sling\.url=*|-Dsling\.url=*)
      SLING_URL="${i#*=}"
      shift # past argument=value
      ;;
      --sling\.user=*|-Dsling\.user=*)
      SLING_USER="${i#*=}"
      shift # past argument=value
      ;;
      --sling\.password=*|-Dsling\.password=*)
      SLING_PASSWORD="${i#*=}"
      shift # past argument=value
      ;;
      --jvm\.args=*|-Djvm\.args=*)
      JVM_ARGS="${i#*=}"
      shift # past argument=value
      ;;
      --display\.pause\.message=*|-Ddisplay\.pause\.message=*)
      DISPLAY_PAUSE_MESSAGE="${i#*=}"
      shift # past argument with no value
      ;;
      --deploy\.core\.components=*|-Ddeploy\.core\.components=*)
      DEPLOY_CORE_COMPONENTS="${i#*=}"
      shift # past argument with no value
      ;;
      build)
      BUILD=true
      DEFAULT_COMMANDS=false
      shift # past argument with no value
      ;;
      deploy)
      DEPLOY=true
      DEFAULT_COMMANDS=false
      ;;
      help)
      HELP=true
      DEFAULT_COMMANDS=false
      shift # past argument with no value
      ;;
      *)
            # unknown option
      ;;
  esac
  done

  if [ "$DEFAULT_COMMANDS" = true ]; then
    BUILD=true
    DEPLOY=true
  fi
}

welcome_message() {
  echo "********************************************************************"
  if ([ "$BUILD" = true ] && [ "$DEPLOY" = true ]) || [ "$HELP" = true ]; then
    echo -e "\e[96m   ___ _   _ ___ _    ___      _     ___  ___ ___ _    _____   __\e[0m"
    echo -e "\e[96m  | _ ) | | |_ _| |  |   \   _| |_  |   \| __| _ \ |  / _ \ \ / /\e[0m"
    echo -e "\e[96m  | _ \ |_| || || |__| |) | |_   _| | |) | _||  _/ |_| (_) \ V /\e[0m"
    echo -e "\e[96m  |___/\___/|___|____|___/    |_|   |___/|___|_| |____\___/ |_|\e[0m"
  elif [ "$BUILD" = true ]; then
    echo -e "\e[96m   ___ _   _ ___ _    ___ \e[0m"
    echo -e "\e[96m  | _ ) | | |_ _| |  |   \\ \e[0m"
    echo -e "\e[96m  | _ \ |_| || || |__| |) |\e[0m"
    echo -e "\e[96m  |___/\___/|___|____|___/\e[0m"
  elif [ "$DEPLOY" = true ]; then
    echo -e "\e[96m   ___  ___ ___ _    _____   __\e[0m"
    echo -e "\e[96m  |   \| __| _ \ |  / _ \ \ / /\e[0m"
    echo -e "\e[96m  | |) | _||  _/ |_| (_) \ V /\e[0m"
    echo -e "\e[96m  |___/|___|_| |____\___/ |_|\e[0m"
  fi
  echo ""
  echo -e "  Profiles: \e[1m${MAVEN_PROFILES}\e[0m"
  echo ""
  echo "********************************************************************"
}

completion_message() {
  echo ""
  if [ "$BUILD" = true ] && [ "$DEPLOY" = true ]; then
    echo -e "*** \e[1mBuild+Deploy complete\e[0m ***"
  elif [ "$BUILD" = true ]; then
    echo -e "*** \e[1mBuild complete\e[0m ***"
  elif [ "$DEPLOY" = true ]; then
    echo -e "*** \e[1mDeploy complete\e[0m ***"
  fi
  echo ""

  pause_message
}

####

execute_build() {
  echo ""
  echo -e "*** \e[1mBuild application\e[0m ***"
  echo ""

  MAVEN_ARGS=""
  if [ -n "$JVM_ARGS" ]; then
    MAVEN_ARGS+="${JVM_ARGS} "
  fi
  if [ -n "${MAVEN_PROFILES}" ]; then
    MAVEN_ARGS+="--activate-profiles ${MAVEN_PROFILES} "
  fi

  mvn $MAVEN_ARGS clean install eclipse:eclipse

  if [ "$?" -ne "0" ]; then
    exit_with_error "*** BUILD FAILED ***"
  fi
}

####

execute_deploy() {
  echo ""
  echo -e "*** \e[1mDeploy to AEM\e[0m ***"
  echo ""

  MAVEN_ARGS=""
  if [ -n "$JVM_ARGS" ]; then
    MAVEN_ARGS+="${JVM_ARGS} "
  fi
  if [ -n "${MAVEN_PROFILES}" ]; then
    MAVEN_ARGS+="--activate-profiles=${MAVEN_PROFILES} "
  fi
  if [ -n "${SLING_URL}" ]; then
    MAVEN_ARGS+="-Dsling.url=${SLING_URL} "
  fi
  if [ -n "${SLING_USER}" ]; then
    MAVEN_ARGS+="-Dsling.user=${SLING_USER} "
  fi
  if [ -n "${SLING_PASSWORD}" ]; then
    MAVEN_ARGS+="-Dsling.password=${SLING_PASSWORD} "
  fi

  if [ "$DEPLOY_CORE_COMPONENTS" = true ]; then
    echo ""
    echo "Deploy Core Components..."
    echo ""
    mvn $MAVEN_ARGS -f examples/content-packages/examples-libs \
        -Dvault.fileList='${project.build.directory}/dependency/core.wcm.components.all.zip' \
        -Dvault.force=true \
        wcmio-content-package:install

    if [ "$?" -ne "0" ]; then
      exit_with_error "*** DEPLOY FAILED ***"
    fi
  fi

  echo ""
  echo "Deploy Core Components examples..."
  echo ""
  mvn $MAVEN_ARGS -f examples/content-packages/examples-libs \
      -Dvault.fileList='${project.build.directory}/dependency/core.wcm.components.examples.ui.config.zip,${project.build.directory}/dependency/core.wcm.components.examples.ui.apps.zip,${project.build.directory}/dependency/core.wcm.components.examples.ui.content.zip' \
      -Dvault.force=true \
      wcmio-content-package:install

  if [ "$?" -ne "0" ]; then
    exit_with_error "*** DEPLOY FAILED ***"
  fi

  echo ""
  echo "Deploy wcm.io Libraries ..."
  echo ""
  mvn $MAVEN_ARGS -f examples/content-packages/examples-libs \
      wcmio-content-package:install

  if [ "$?" -ne "0" ]; then
    exit_with_error "*** DEPLOY FAILED ***"
  fi

  echo ""
  echo "Deploy wcm.io WCM Core Components examples application..."
  echo ""
  mvn $MAVEN_ARGS -f examples/content-packages/examples \
      wcmio-content-package:install

  if [ "$?" -ne "0" ]; then
    exit_with_error "*** DEPLOY FAILED ***"
  fi

  echo ""
  echo "Deploy wcm.io WCM Core Components examples content..."
  echo ""
  mvn $MAVEN_ARGS -f examples/content-packages/examples-sample-content \
      wcmio-content-package:install

  if [ "$?" -ne "0" ]; then
    exit_with_error "*** DEPLOY FAILED ***"
  fi

}

####

# Display a pause message (only when the script was executed via double-click on windows)
pause_message() {
  if [ "$DISPLAY_PAUSE_MESSAGE" = true ]; then
    read -n1 -r -p "Press any key to continue..."
  fi
}

# Displays error message and exit the script with error code
exit_with_error() {
  echo ""
  echo -e "\e[91m$1\e[0m" 1>&2
  echo ""
  pause_message
  exit 1
}

####

parse_parameters "$@"
welcome_message
if [ "$HELP" = true ]; then
  help_message_exit
fi
if [ "$BUILD" = true ]; then
  execute_build
fi
if [ "$DEPLOY" = true ]; then
  execute_deploy
fi
completion_message
