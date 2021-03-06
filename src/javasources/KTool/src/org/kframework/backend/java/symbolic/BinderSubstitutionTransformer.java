package org.kframework.backend.java.symbolic;

import com.google.common.collect.ImmutableList;
import org.kframework.backend.java.kil.*;
import org.kframework.kil.ASTNode;

import java.util.Map;
import java.util.Set;


/**
 * Substitutes variables with terms according to a given substitution map using binders.
 * 
 * @author TraianSF
 */
public class BinderSubstitutionTransformer extends SubstitutionTransformer {

    public BinderSubstitutionTransformer(Map<Variable, ? extends Term> substitution, TermContext context) {
    	super(substitution, context);
        preTransformer.addTransformer(new BinderSubstitution(context));
    }

    /**
     * Checks
     *
     */
    private class BinderSubstitution extends LocalTransformer {
        public BinderSubstitution(TermContext context) {
            super(context);
        }

        @Override
        public ASTNode transform(KItem kItem) {
            KLabel kLabel = kItem.kLabel();
            KList kList = kItem.kList();
            if (kLabel instanceof KLabelConstant) {
                KLabelConstant kLabelConstant = (KLabelConstant) kLabel;
                if (kLabelConstant.isBinder()) {
                    assert kList.getItems().size()==2 && !kList.hasFrame() :
                            "Only supporting binders of the form lambda x. e for now";
                    Term boundVars = kList.get(0);
//                    if (boundVars instanceof Variable ||
//                            boundVars instanceof BuiltinList || boundVars instanceof BuiltinSet) {
                        // only rename vars if they are already a builtin structure.
                        Term bindingExp = kList.get(1);
                        Set<Variable> variables = boundVars.variableSet();
                        Map<Variable,Variable> freshSubstitution = Variable.getFreshSubstitution(variables);
                        Term freshBoundVars = boundVars.substitute(freshSubstitution, context);
                        Term freshbindingExp = bindingExp.substitute(freshSubstitution, context);
                        kList = new KList(ImmutableList.<Term>of(freshBoundVars,freshbindingExp));
                        kItem = new KItem(kLabel, kList, context.definition().context());
//                    }
                }
            }
            return super.transform(kItem);
        }
    }
}
