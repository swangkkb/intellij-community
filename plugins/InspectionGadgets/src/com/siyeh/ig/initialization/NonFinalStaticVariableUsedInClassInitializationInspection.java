/*
 * Copyright 2003-2006 Dave Griffith, Bas Leijdekkers
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
package com.siyeh.ig.initialization;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.siyeh.InspectionGadgetsBundle;
import com.siyeh.ig.BaseInspection;
import com.siyeh.ig.BaseInspectionVisitor;
import org.jetbrains.annotations.NotNull;

public class NonFinalStaticVariableUsedInClassInitializationInspection
        extends BaseInspection {

    public String getDisplayName(){
        return InspectionGadgetsBundle.message(
                "non.final.static.variable.initialization.display.name");
    }

    public String getGroupDisplayName(){
        return GroupNames.INITIALIZATION_GROUP_NAME;
    }

    @NotNull
    public String buildErrorString(Object... infos){
        return InspectionGadgetsBundle.message(
                "non.final.static.variable.initialization.problem.descriptor");
    }

    public BaseInspectionVisitor buildVisitor(){
        return new NonFinalStaticVariableUsedInClassInitializationVisitor();
    }

    private static class NonFinalStaticVariableUsedInClassInitializationVisitor
            extends BaseInspectionVisitor{

        public void visitReferenceExpression(PsiReferenceExpression expression){
            super.visitReferenceExpression(expression);
            if(!isInClassInitialization(expression)){
                return;
            }
            final PsiElement referent = expression.resolve();
            if(!(referent instanceof PsiField)){
                return;
            }
            final PsiField field = (PsiField) referent;
            if(!field.hasModifierProperty(PsiModifier.STATIC)){
                return;
            }
            if(field.hasModifierProperty(PsiModifier.FINAL)){
                return;
            }
            registerError(expression);
        }

        private static boolean isInClassInitialization(
                PsiExpression expression){
            final PsiClass expressionClass =
                    PsiTreeUtil.getParentOfType(expression, PsiClass.class);
            final PsiMember member =
                    PsiTreeUtil.getParentOfType(expression,
                            PsiClassInitializer.class, PsiField.class);
            if (member == null) {
                return false;
            }
            final PsiClass memberClass = member.getContainingClass();
            if (!memberClass.equals(expressionClass)) {
                return false;
            }
            if (!member.hasModifierProperty(PsiModifier.STATIC)) {
                return false;
            }
            if (member instanceof PsiClassInitializer) {
                return !PsiUtil.isOnAssignmentLeftHand(expression);
            }
            return true;
        }
    }
}