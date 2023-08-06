package pascal.taie.analysis.pta.plugin.taint.inferer.strategy;

import pascal.taie.analysis.pta.plugin.taint.inferer.InfererContext;
import pascal.taie.analysis.pta.plugin.taint.inferer.InferredTransfer;
import pascal.taie.language.classes.ClassHierarchy;
import pascal.taie.language.classes.JClass;
import pascal.taie.language.classes.JMethod;
import pascal.taie.language.type.ClassType;
import pascal.taie.util.collection.Sets;

import java.util.Set;
import java.util.stream.Collectors;

public class IgnoreException implements TransInferStrategy {

    public static final String ID = "ignore-exception";

    private Set<JClass> throwableSubClasses;

    @Override
    public void setContext(InfererContext context) {
        ClassHierarchy hierarchy = context.solver().getHierarchy();
        JClass throwableClass = hierarchy.getJREClass("java.lang.Throwable");
        throwableSubClasses = Sets.newSet(hierarchy.getAllSubclassesOf(throwableClass));
    }

    @Override
    public boolean shouldIgnore(JMethod method, int index) {
        return throwableSubClasses.contains(method.getDeclaringClass());
    }

    @Override
    public Set<InferredTransfer> apply(JMethod method, int index, Set<InferredTransfer> transfers) {
        return transfers.stream()
                .filter(tf -> {
                    if(tf.getType() instanceof ClassType classType) {
                        return !throwableSubClasses.contains(classType.getJClass());
                    }
                    return true;
                })
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public int getPriority() {
        return 11;
    }
}
