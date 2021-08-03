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
SLING_URL="http://localhost:4502"
SLING_USER="admin"
SLING_PASSWORD="admin"
JVM_ARGS=""

# display pause message only when script was executed via double-click on windows
if [[ $0 == *":\\"* ]]; then
  DISPLAY_PAUSE_MESSAGE=true
fi
BUILD=false
DEPLOY=false
HELP=false
DEFAULT_COMMANDS=true
DEPLOY_CORE_COMPONENTS=true

####

help_message_exit() {
  echo ""
  echo "  Syntax <parameters> <commands>"
  echo ""
  echo "  Parameters:"
  echo "    --sling.url=${SLING_URL}"
  echo "    --sling.user=${SLING_USER}"
  echo "    --sling.password=${SLING_PASSWORD}"
  echo "    --jvm.args=${JVM_ARGS}"
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
  echo -e "********************************************************************\e[96m"
  if ([ "$BUILD" = true ] && [ "$DEPLOY" = true ]) || [ "$HELP" = true ]; then
    echo "   ___ _   _ ___ _    ___      _     ___  ___ ___ _    _____   __"
    echo "  | _ ) | | |_ _| |  |   \   _| |_  |   \| __| _ \ |  / _ \ \ / /"
    echo "  | _ \ |_| || || |__| |) | |_   _| | |) | _||  _/ |_| (_) \ V /"
    echo "  |___/\___/|___|____|___/    |_|   |___/|___|_| |____\___/ |_|"
  elif [ "$BUILD" = true ]; then
    echo "   ___ _   _ ___ _    ___ "
    echo "  | _ ) | | |_ _| |  |   \\"
    echo "  | _ \ |_| || || |__| |) |"
    echo "  |___/\___/|___|____|___/"
  elif [ "$DEPLOY" = true ]; then
    echo "   ___  ___ ___ _    _____   __"
    echo "  |   \| __| _ \ |  / _ \ \ / /"
    echo "  | |) | _||  _/ |_| (_) \ V /"
    echo "  |___/|___|_| |____\___/ |_|"
  fi
  echo -e "\e[0m"
  echo -e "  Destination: \e[1m${SLING_URL}\e[0m"
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

  mvn ${JVM_ARGS} \
      -Pfast clean install eclipse:eclipse

  if [ "$?" -ne "0" ]; then
    exit_with_error "*** BUILD FAILED ***"
  fi
}

####

execute_deploy() {
  echo ""
  echo -e "*** \e[1mDeploy to AEM\e[0m ***"
  echo ""

  if [ "$DEPLOY_CORE_COMPONENTS" = true ]; then
    echo ""
    echo "Deploy Core Components..."
    echo ""
    mvn -f examples/content-packages/examples \
        -Dvault.fileList='${project.build.directory}/dependency/core.wcm.components.all.zip' \
        -Dvault.force=true \
        ${JVM_ARGS} \
        -Dsling.url=${SLING_URL} \
        -Dsling.user=${SLING_USER} \
        -Dsling.password=${SLING_PASSWORD} \
        wcmio-content-package:install

    if [ "$?" -ne "0" ]; then
      exit_with_error "*** DEPLOY FAILED ***"
    fi
  fi

  echo ""
  echo "Deploy Core Components examples..."
  echo ""
  mvn -f examples/content-packages/examples \
      -Dvault.fileList='${project.build.directory}/dependency/core.wcm.components.examples.ui.config.zip,${project.build.directory}/dependency/core.wcm.components.examples.ui.apps.zip,${project.build.directory}/dependency/core.wcm.components.examples.ui.content.zip' \
      -Dvault.force=true \
      ${JVM_ARGS} \
      -Dsling.url=${SLING_URL} \
      -Dsling.user=${SLING_USER} \
      -Dsling.password=${SLING_PASSWORD} \
      wcmio-content-package:install

  if [ "$?" -ne "0" ]; then
    exit_with_error "*** DEPLOY FAILED ***"
  fi

  echo ""
  echo "Deploy wcm.io WCM Core Components examples application..."
  echo ""
  mvn -f examples/content-packages/examples \
      ${JVM_ARGS} \
      -Dsling.url=${SLING_URL} \
      -Dsling.user=${SLING_USER} \
      -Dsling.password=${SLING_PASSWORD} \
      wcmio-content-package:install

  if [ "$?" -ne "0" ]; then
    exit_with_error "*** DEPLOY FAILED ***"
  fi

  echo ""
  echo "Deploy wcm.io WCM Core Components examples content..."
  echo ""
  mvn -f examples/content-packages/examples-sample-content \
      ${JVM_ARGS} \
      -Dsling.url=${SLING_URL} \
      -Dsling.user=${SLING_USER} \
      -Dsling.password=${SLING_PASSWORD} \
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
