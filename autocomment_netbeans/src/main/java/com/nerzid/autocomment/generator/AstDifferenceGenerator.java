/*
 * Copyright 2016 nerzid.
 *
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
 */
package com.nerzid.autocomment.generator;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.Delete;
import com.github.gumtreediff.actions.model.Insert;
import com.github.gumtreediff.actions.model.Move;
import com.github.gumtreediff.actions.model.Update;
import com.github.gumtreediff.tree.ITree;
import gumtree.spoon.AstComparator;
import gumtree.spoon.builder.SpoonGumTreeBuilder;
import gumtree.spoon.diff.Diff;
import gumtree.spoon.diff.operations.Operation;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import spoon.compiler.SpoonCompiler;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

/**
 *
 * @author nerzid
 */
public class AstDifferenceGenerator {

    static Factory factory;

    public static void main(String[] args) throws FileNotFoundException {
        factory = new FactoryImpl(new DefaultCoreFactory(), new StandardEnvironment());

        File f1 = new File("c:/users/nerzid/desktop/test3.java");
        File f2 = new File("c:/users/nerzid/desktop/test.java");

        AstComparator a = new AstComparator();

        SpoonGumTreeBuilder sgtb = new SpoonGumTreeBuilder();

        ITree tree = sgtb.getTree(getCtType(f1));
        ITree tree2 = sgtb.getTree(getCtType(f2));
        String treeStrng = "";
        //System.out.println(tree2.toTreeString());
        try {
            Diff df = a.compare(f1, f2);

            System.out.println(df.toString());
            System.out.println("*********");

            List<Operation> li = df.getAllOperations();

            double difCount = li.size();
            double t1Count = tree.getLeaves().size();
            double t2Count = tree2.getLeaves().size();

            System.out.println("diff: " + difCount);
            System.out.println("t1count: " + t1Count);
            System.out.println("t2count: " + t2Count);

            double percnt = difCount / (t1Count + t2Count) * 100;

            System.out.println("Similiarity Percentage: " + Math.abs(percnt - 100));
            if (li.isEmpty()) {
                System.out.println("No differences");
            } else {
                System.out.println("Differences:");
                int count_upd = 0;
                int count_ins = 0;
                int count_mv = 0;
                int count_dlt = 0;

                for (Operation operation : li) {
                    //System.out.print("Element: " + operation.getNode().toString());
//                    String s = operation.getAction().toString();
//                    System.out.println("Action: " + s);
                    Action act = operation.getAction();
                    ITree node = operation.getAction().getNode();
                    //System.out.println("TREEEE");
                    treeStrng = node.toTreeString();
                    String oInfo = "Action: ";
                    if (act instanceof Update) {
                        Update upd = (Update) act;

                        oInfo += "UPDATE -> ";
                        oInfo += "from " + upd.getNode().getLabel() + " ";
                        if (operation.getNode() != null) {
                            oInfo += "with type(" + operation.getNode().getClass().getSimpleName() + ")";
                        }
                        oInfo += "to " + upd.getValue();
                        count_upd++;
                    } else if (act instanceof Insert) {
                        Insert ins = (Insert) act;

                        oInfo += "INSERT -> ";
                        oInfo += ins.getNode().getLabel() + " ";
                        if (operation.getNode() != null) {
                            oInfo += "with type(" + operation.getNode().getClass().getSimpleName() + ")";
                        }
                        count_ins++;
                    } else if (act instanceof Move) {
                        Move mv = (Move) act;

                        oInfo += "MOVE -> ";
                        oInfo += mv.getNode().getLabel() + " ";
                        if (operation.getNode() != null) {
                            oInfo += "with type(" + operation.getNode().getClass().getSimpleName() + ")";
                        }
                        count_mv++;
                    } else if (act instanceof Delete) {
                        Delete dlt = (Delete) act;

                        oInfo += "DELETE -> ";
                        oInfo += dlt.getNode().getLabel() + " ";
                        if (operation.getNode() != null) {
                            oInfo += "with type(" + operation.getNode().getClass().getSimpleName() + ")";
                        }
                        count_dlt++;
                    }
                    System.out.println(oInfo);
                }
                System.out.println("");
                System.out.println("ACTIONS STATISTICS");
                System.out.println("UPDATE: " + count_upd);
                System.out.println("INSERT: " + count_ins);
                System.out.println("MOVE: " + count_mv);
                System.out.println("DELETE: " + count_dlt);
            }

        } catch (Exception ex) {
            System.out.println("Error msg: " + ex.getMessage());
        }

    }

    public static CtType getCtType(File file) throws FileNotFoundException {
        SpoonCompiler compiler = new JDTBasedSpoonCompiler(factory);
        compiler.getFactory().getEnvironment().setLevel("OFF");
        compiler.addInputSource(SpoonResourceHelper.createResource(file));
        compiler.build();
        return factory.Type().getAll().get(0);
    }
}
