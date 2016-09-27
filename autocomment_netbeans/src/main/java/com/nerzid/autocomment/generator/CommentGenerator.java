package com.nerzid.autocomment.generator;

/**
 *
 * @author nerzid
 */
public class CommentGenerator {


    public static void main(String[] args) {
        
    }

    

// Only avaliable for New spoon api
//    /**
//     * Prints model(AST) as String. Looks bad, needs some refactoring.
//     *
//     * @param l To get model and maybe some other components
//     */
//    public static void printModel(Launcher l) {
//        CtModel model = l.getModel();
//
//        Collection<CtType<?>> ls = model.getAllTypes();
//        
//        for (CtType<?> type : ls) {
//            System.out.println(type.getClass().getSimpleName() + " -> " + type.getShortRepresentation());
//            System.out.println("-");
//            for (CtField<?> field : type.getFields()) {
//                System.out.print("\t-");
//                System.out.println(field.getClass().getSimpleName() + " -> " + field.getShortRepresentation());
//            }
//
//            for (CtMethod<?> method : type.getMethods()) {
//                System.out.print("\t-");
//                System.out.println(method.getClass().getSimpleName() + " -> " + method.getShortRepresentation());
//                for (CtStatement stmt : method.getBody().getStatements()) {
//                    System.out.print("\t\t-");
//                    System.out.println(stmt.getClass().getSimpleName() + " -> " + stmt.getShortRepresentation());
//                    
//                }
//                
//            }
//        }
//    }
}
