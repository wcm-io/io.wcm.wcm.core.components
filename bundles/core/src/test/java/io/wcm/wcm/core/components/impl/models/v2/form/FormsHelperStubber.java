/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2020 wcm.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package io.wcm.wcm.core.components.impl.models.v2.form;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * AEM foundation FormsHelper cannot be used directly in unit tests due to references to
 * internal classes that are not part of the API JAR. Stub around this problem.
 */
final class FormsHelperStubber {

  // the class to stub
  private static final String CLASS_NAME = "com.day.cq.wcm.foundation.forms.FormsHelper";

  // the static field which is causing problems with class loading due to missing impl class
  private static final String ERROR_FIELD = "defaultFormStructureHelper";

  private FormsHelperStubber() {
    // static methods only
  }

  static void createStub() {
    ClassPool classPool = ClassPool.getDefault();
    CtClass ctClass;
    try {
      ctClass = classPool.get(CLASS_NAME);
      // indicates the class has already been stubbed and loaded
      if (ctClass.isFrozen()) {
        return;
      }
      // set the body of all methods in the class to empty,
      // to remove any dependencies on impl classes.
      for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
        ctMethod.setBody(null);
      }
      // remove the error causing static field declaration
      ctClass.removeField(ctClass.getDeclaredField(ERROR_FIELD));
      // remove the static initializer block calling new on impl class.
      ctClass.removeConstructor(ctClass.getClassInitializer());
      // load the stubbed class
      ctClass.toClass();
    }
    catch (NotFoundException | CannotCompileException ex) {
      throw new RuntimeException(ex);
    }
  }

}
