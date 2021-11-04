#!/bin/bash
# #%L
#  wcm.io
#  %%
#  Copyright (C) 2020 wcm.io
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


SLING_URL="http://localhost:45025"

if [[ $0 == *":\\"* ]]; then
  DISPLAY_PAUSE_MESSAGE=true
fi

# install AEM 6.5 with service pack
mvn --non-recursive wcmio-content-package:install \
    -Dvault.artifact=adobe.binary.aem.65.servicepack:aem-service-pkg:zip:6.5.10 \
    -Dvault.delayAfterInstallSec=30 \
    -Dsling.url=${SLING_URL}

if [ "$?" -ne "0" ]; then
  exit
fi

./build-deploy.sh --sling.url=${SLING_URL} --display.pause.message=${DISPLAY_PAUSE_MESSAGE} "$@"
