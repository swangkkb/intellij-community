/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.intellij.build
/**
 * @author nik
 */
class ApplicationInfoProperties {
  final String majorVersion
  final String minorVersion
  final String shortProductName
  final String minorVersionMainPart
  final String productName
  final String companyName
  final boolean isEAP

  @SuppressWarnings(["GrUnresolvedAccess", "GroovyAssignabilityCheck"])
  ApplicationInfoProperties(String appInfoXmlPath) {
    def root = new XmlParser().parse(new File(appInfoXmlPath))
    majorVersion = root.version.first().@major
    minorVersion = root.version.first().@minor
    shortProductName = root.names.first().@product
    productName = root.names.first().@fullname
    companyName = root.company.first().@name
    minorVersionMainPart = minorVersion.takeWhile { it != '.' }
    isEAP = Boolean.parseBoolean(root.version.first().@eap)
  }

  public String getUpperCaseProductName() { shortProductName.toUpperCase() }
}