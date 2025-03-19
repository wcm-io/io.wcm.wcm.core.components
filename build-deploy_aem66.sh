#!/bin/bash
# #%L
#  wcm.io
#  %%
#  Copyright (C) 2022 wcm.io
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

MAVEN_PROFILES="fast,aem66"

if [[ $0 == *":\\"* ]]; then
  DISPLAY_PAUSE_MESSAGE=true
fi

./build-deploy.sh --deploy.core.components=true --maven.profiles=${MAVEN_PROFILES} --display.pause.message=${DISPLAY_PAUSE_MESSAGE} "$@"
