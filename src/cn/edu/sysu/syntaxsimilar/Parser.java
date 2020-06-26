package cn.edu.sysu.syntaxsimilar;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.Statement;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

import java.util.Set;
import java.util.HashSet;

public class Parser {

    private static ArrayList<TypeInfo> variableMap = 
        new ArrayList<TypeInfo>();
 
    private static int scopeLevel = 0;

    static class TypeInfo {
        public String varName;
        public int level;
        public String varType;

        TypeInfo(String varName, int level, String varType) { 
            this.varName = varName; 
            this.level = level; 
            this.varType = varType;
        }

        public static String getMappedType(String variableName, ArrayList<TypeInfo> mapList) {
            
            //for (TypeInfo map : mapList) {
            //    System.out.println(map.varName +  " " + map.varType);
           // }

            for (int i = mapList.size() - 1; i >= 0; i--) {
                TypeInfo map = mapList.get(i);
                if (map.varName.equals(variableName)) {
                    return map.varType;
                }
            }
            return null;
        }
    }


    /* Convert a file into a String */
    private static String fileToString(String path) throws IOException {
               
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, StandardCharsets.UTF_8);
    }

    
    private static void clearLocalVariables() {
        for (int i = variableMap.size()-1; i >= 0; i--) {

            if (variableMap.get(i).level == scopeLevel) {
                //variableMap.remove(variableMap.get(i));
                variableMap.remove(i);
            }
        }
    }

	/* Convert files that are in String format to AST */
    public static Tokenizer parseAST2Tokens(String absPath, int minNumLines, boolean debug,String basePath,int verNum) {
        String source = "";
        try {
            source = fileToString(absPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ASTParser parser = ASTParser.newParser(AST.JLS4);

        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        char[] content = source.toCharArray();
        parser.setSource(content);
        parser.setUnitName(absPath);
        Map<String, String> options = JavaCore.getOptions();
        options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_7);
        options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM,
            JavaCore.VERSION_1_7);
        options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_7);
        
        // new String[] {"UTF-8"}
        String[] sources = {};
        String[] classPaths = {};
        parser.setEnvironment(classPaths, sources,
            null, true);
        //parser.setBindingsRecovery(true);
        parser.setResolveBindings(false);
        parser.setCompilerOptions(options);
        parser.setStatementsRecovery(true);

        //System.out.println(absPath);

        try {
            final CompilationUnit unit = (CompilationUnit) parser.createAST(null);
            final AST ast = unit.getAST();

            // Process the main body
            final Tokenizer tk = new Tokenizer(minNumLines, debug);
            try {
                unit.accept(new ASTVisitor() {
                
                    /*
                    // JavaDoc comment that starts with "/** for type declarations"
                    public boolean	visit(AnnotationTypeDeclaration node) {
                        
                        int startLine = unit.getLineNumber(node.getStartPosition());
                        return true;
                    }
                    
                    // JavaDoc comment that starts with "/** for type member declarations"
                    public boolean	visit(AnnotationTypeMemberDeclaration node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        return true;
                    }
                    */

                    public boolean	visit(AnonymousClassDeclaration node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash(TokenType.AnonymousClassDeclaration.ordinal(), startLine,basePath,verNum);

                        return true;
                    }
                    
                    public boolean	visit(ArrayAccess node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash(TokenType.ArrayAccess.ordinal(), startLine,basePath,verNum);

                        return true;
                    }

                    public boolean	visit(ArrayCreation node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash(TokenType.ArrayCreation.ordinal(), startLine,basePath,verNum);
                        return true;
                    }

                    public boolean	visit(ArrayInitializer node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash(TokenType.ArrayInitializer.ordinal(), startLine,basePath,verNum);

                        return true;
                    }

                    public boolean	visit(ArrayType node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        ITypeBinding binding = node.getElementType().resolveBinding();
                        if (binding != null) {
                            tk.addHash(TokenType.ArrayType.ordinal(),binding.getQualifiedName(), startLine,basePath,verNum);
                        }
                        else {
                            tk.addHash(TokenType.ArrayType.ordinal(), startLine,basePath,verNum);
                        }

                        return true;
                    }

                    public boolean	visit(AssertStatement node) {
                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);
                        tk.addHash(TokenType.AssertStatement.ordinal(), startLine,basePath,verNum);

                        return true;
                    }

                    public void endVisit(AssertStatement node) {
                    	int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.statementEnd(scopeLevel, startLine, basePath,  verNum);
                    }

                    public boolean	visit(Assignment node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());				
                        String operator = node.getOperator().toString();
                        tk.addHash(TokenType.Assignment.ordinal(), operator, startLine,basePath,verNum);

                        return true;
                    }
                    
                    public boolean	visit(Block node) {
                        scopeLevel = scopeLevel + 1;
                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.statementEnd(scopeLevel, startLine, basePath,  verNum);

                        return true;
                    }

                    public void endVisit(Block node) {
                        clearLocalVariables();

                        scopeLevel = scopeLevel - 1;
                    }

                    public boolean	visit(BooleanLiteral node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        if (node.booleanValue()) {
                            tk.addHash(TokenType.BooleanLiteral.ordinal(), "true", startLine,basePath,verNum);
                        } else {
                            tk.addHash(TokenType.BooleanLiteral.ordinal(), "false", startLine,basePath,verNum);
                        }
                        return true;
                    }

                    public boolean	visit(BreakStatement node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);
                        tk.addHash(TokenType.BreakStatement.ordinal(), startLine,basePath,verNum);

                        return true;
                    }

                    public void endVisit(BreakStatement node) {
                    	int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.statementEnd(scopeLevel, startLine, basePath,  verNum);
                    }

                    public boolean	visit(CastExpression node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash(TokenType.CastExpression.ordinal(), startLine,basePath,verNum); //reminder
                        return true;
                    }

                    public boolean	visit(CatchClause node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);
                        tk.addHash(TokenType.CatchClause.ordinal(), startLine,basePath,verNum);

                        return true;
                    }

                    public boolean	visit(CharacterLiteral node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash(TokenType.CharacterLiteral.ordinal(), node.getEscapedValue(), startLine,basePath,verNum);
                        return true;
                    }

                    public boolean	visit(ClassInstanceCreation node) {

                        scopeLevel = scopeLevel + 1;

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash(TokenType.ClassInstanceCreation.ordinal(), startLine,basePath,verNum);
                        return true;
                    }

                    public void endVisit(ClassInstanceCreation node) {
                        scopeLevel = scopeLevel - 1;
                    }

                    /*
                    public boolean	visit(CompilationUnit node) {
                        int startLine = unit.getLineNumber(node.getStartPosition());
                        return true;
                    }
                    */

                    public boolean	visit(ConditionalExpression node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash(TokenType.ConditionalExpression.ordinal(), startLine,basePath,verNum);

                        return true;
                    }

                    public boolean	visit(ConstructorInvocation node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);
                        tk.addHash(TokenType.SuperConstructorInvocation.ordinal(), startLine,basePath,verNum);

                        return true;
                    }

                    public void endVisit(ConstructorInvocation node) {
                    	int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.statementEnd(scopeLevel, startLine, basePath,  verNum);
                    }

                    public boolean	visit(ContinueStatement node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);
                        tk.addHash(TokenType.ContinueStatement.ordinal(), startLine,basePath,verNum);

                        return true;
                    }

                    public void endVisit(ContinueStatement node) {
                    	int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.statementEnd(scopeLevel, startLine, basePath,  verNum);
                    }

                    public boolean	visit(DoStatement node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());	
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);
                        tk.addHash(TokenType.DoStatement.ordinal(), startLine,basePath,verNum);

                        return true;
                    }

                    public void endVisit(DoStatement node) {
                    	int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.statementEnd(scopeLevel, startLine, basePath,  verNum);
                    }

                    /*
                    public boolean	visit(EmptyStatement node) {
                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.statementStart(startLine);

                        tk.addHash(78, startLine);
                        tk.statementEnd(scopeLevel);

                        return true;
                    }*/

                    public boolean	visit(EnhancedForStatement node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);
                        tk.addHash(TokenType.EnhancedForStatement.ordinal(), startLine,basePath,verNum);

                        return true;
                    }

                    /*
                    public boolean	visit(EnumConstantDeclaration node) {
                        System.out.println("EnumConstantDeclaration");

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        System.out.println("EnumConstantDeclaration at line "+ Integer.toString(startLine));
                        return true;
                    }*/

                    /*
                    public boolean	visit(EnumDeclaration node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        return true;
                    }
                    */

                    public boolean	visit(ExpressionStatement node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);

                        Expression exp = node.getExpression();

                        return true;
                    }

                    public void endVisit(ExpressionStatement node) {
                    	int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.statementEnd(scopeLevel, startLine, basePath,  verNum);
                    }

                    public boolean	visit(FieldAccess node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        String fieldName = node.getName().toString();
                        tk.addHash(TokenType.FieldAccess.ordinal(), fieldName, startLine,basePath,verNum);

                        return true;
                    }
                    /*
                     * always together with VariableDeclarationFragment
                     */
                    public boolean	visit(FieldDeclaration node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());

                        Type type = node.getType();
                        ITypeBinding binding = type.resolveBinding();

                        List<VariableDeclarationFragment> fragments = node.fragments();
                        for (VariableDeclarationFragment fragment : fragments) {
                            String name = fragment.getName().toString();
                            if (binding != null) {

                                TypeInfo typeInfo = new TypeInfo(
                                        name, scopeLevel, binding.getName());

                                variableMap.add(typeInfo);
                                /*
                                if (!within_method.isEmpty()) {
                                    // In a method
                                    tk.statementStart(startLine);
                                    tk.addHash(85, binding.getName(), startLine);
                                }
                                */
                            } else {

                                String varType = node.getType().toString();
                                TypeInfo typeInfo = new TypeInfo(
                                        name, scopeLevel, varType);

                                variableMap.add(typeInfo);
                                /*
                                if (!within_method.isEmpty()) {
                                    // In a method
                                    tk.addHash(85, varType, startLine);
                                }
                                */
                            }
                        }
                        return true;
                    }

                    public boolean	visit(ForStatement node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);

                        tk.addHash(TokenType.ForStatement.ordinal(),startLine,basePath,verNum);

                        return true;
                    }

                    public boolean	visit(IfStatement node) {
                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);
                        ASTNode parent = node.getParent();
                        boolean if_else_happen = false;
                        if (parent != null) {
                            if (parent.getNodeType() == 25) {
                                Statement else_statement = ((IfStatement) parent).getElseStatement();
                                if (else_statement != null) {
                                    if (else_statement.equals(node)) {
                                        if_else_happen = true;
                                    }
                                }
                            }
                        }

                        if (if_else_happen) {
                            tk.addHash(TokenType.Elseif.ordinal(), startLine,basePath,verNum);
                        } else {
                            tk.addHash(TokenType.IfStatement.ordinal(),startLine,basePath,verNum);
                        }

                        return true;
                    }
                    
                    public void	endVisit(IfStatement node) {
                        int startLine = unit.getLineNumber(node.getStartPosition()+node.getLength());
                        ASTNode parent = node.getParent();
                        if (parent != null) {
                            if (parent.getNodeType() == 25) {
                                Statement else_statement = ((IfStatement) parent).getElseStatement();
                                if (else_statement != null) {
                                    if (else_statement.equals(node)) {
                                        return;
                                    }
                                }
                            }
                        }
                    }
                    
                    public void	preVisit(ASTNode node) {
                        ASTNode parent = node.getParent();
                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        
                        if (parent != null) {
                            if (parent.getNodeType() == 25 && node.getNodeType() != 25) {
                                Statement else_statement = ((IfStatement) parent).getElseStatement();
                                if (else_statement != null) {
                                    if (else_statement.equals(node)) {
                                        tk.statementStart(startLine, endLine);
                                        tk.addHash(TokenType.Else.ordinal(), startLine,basePath,verNum);
                                        tk.statementEnd(scopeLevel, startLine, basePath,  verNum);
                                    }
                                }
                            }
                        }
                    }
                    
                    public boolean	visit(InfixExpression node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        String operator = node.getOperator().toString();
                        tk.addHash(TokenType.InfixExpression.ordinal(), operator,startLine,basePath,verNum);

                        return true;
                    }

                    /*
                    public boolean	visit(Initializer node) {
                        System.out.println("Initializer");

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash(28,startLine);

                        return true;
                    }*/

                    public boolean	visit(InstanceofExpression node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash(TokenType.InstanceofExpression.ordinal(),startLine,basePath,verNum);
                        return true;
                    }

                    /* Comments */
        //			public boolean	visit(Javadoc node) {
        //
        //				int startLine = unit.getLineNumber(node.getStartPosition());
        //
        //				System.out.println("Javadoc at line "+ Integer.toString(startLine));
        //
        //				return true;
        //			}

                    public boolean	visit(LabeledStatement node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);
                        tk.addHash(TokenType.LabeledStatement.ordinal(), startLine,basePath,verNum);
                        tk.statementEnd(scopeLevel, startLine, basePath,  verNum);

                        return true;
                    }
                    
                    /*
                    public boolean visit(Annotation node) {

                        System.out.println("annotation node");

                        return true;

                    }

                    public boolean	visit(MarkerAnnotation node) {

                        return false;
                        //int startLine = unit.getLineNumber(node.getStartPosition());
                        //tk.addHash(31, node.getTypeName().getFullyQualifiedName(),startLine);

                        //return true;
                    }
                    */

                    public boolean	visit(MemberRef node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash(TokenType.MemberRef.ordinal(), node.resolveBinding().getName(), startLine,basePath,verNum);

                        return true;
                    }

                    public boolean	visit(MemberValuePair node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash(TokenType.MemberValuePair.ordinal(), startLine,basePath,verNum);

                        return true;
                    }

                    public boolean	visit(MethodDeclaration node) {
                        tk.inMethod = true;

                        // Get Line number
                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        
                        tk.methodStart(node.getName().toString(), startLine);

                        tk.statementStart(startLine, endLine);

                        tk.addHash(TokenType.MethodDeclaration.ordinal(), startLine,basePath,verNum);

                        return true;
                    }
                    
                    public void endVisit(MethodDeclaration node) {
                        tk.inMethod = false;

                        int startLine = unit.getLineNumber(node.getStartPosition()+node.getLength());
                        //tk.statementStart(startLine);
                        //tk.addHash(75, startLine);
                        //tk.statementEnd(scopeLevel);

                        //IMethodBinding methodBinding = node.resolveBinding();
                        tk.methodEnd(node.getName().toString(), startLine);
                        //System.out.println(node.getName().toString());
                    }

                    public boolean	visit(MethodInvocation node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash(TokenType.MethodInvocation.ordinal(), startLine,basePath,verNum);
                        tk.hasMethodInvocation();

                        return true;
                    }

                    /*
                    public boolean	visit(MethodRef node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());

                        System.out.println("MethodRef at line "+ Integer.toString(startLine));

                        return true;
                    }

                    public boolean	visit(MethodRefParameter node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());

                        System.out.println("MethodRefParameter at line "+ Integer.toString(startLine));

                        return true;
                    }*/


                    /*	public, protected, private, static, abstract, final, native, synchronized, transient
                     *  volatile, strictfp
                     */
                    /*
                    public boolean	visit(Modifier node) {
                        System.out.println("modifier node");
                        return true;
                    }
                    */
                 

                    /*
                    public boolean	visit(NormalAnnotation node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        System.out.println("Initializer at line "+ Integer.toString(startLine));
                        return true;
                    }
                    */

                    public boolean	visit(NullLiteral node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash(TokenType.NullLiteral.ordinal(), startLine,basePath,verNum);
                        return true;
                    }

                    public boolean	visit(NumberLiteral node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash(TokenType.NumberLiteral.ordinal(), startLine,basePath,verNum);

                        //System.out.println("NumberLiteral at line "+ Integer.toString(startLine));

                        return true;
                    }

                    public boolean visit(ImportDeclaration node) {
                        return false;
                    }

                    public boolean	visit(PackageDeclaration node) {
                        return false;
        //
        //				//int startLine = unit.getLineNumber(node.getStartPosition());
        //
        ////				SimpleName name = (SimpleName) node.getName();
        ////				System.out.println("PackageDeclaration with name of "+name+" at line "+ unit.getLineNumber(name.getStartPosition()));
        //				System.out.println("PackageDeclaration with name of ");
        //
        //				return true;
                    }

                    /*
                    public boolean	visit(ParameterizedType node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash(43, startLine);

                        return true;
                    }*/

                    public boolean	visit(ParenthesizedExpression node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());				
                        tk.addHash(TokenType.ParenthesizedExpression.ordinal(), startLine,basePath,verNum);
                        return true;
                    } 

                    public boolean	visit(PostfixExpression node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        Expression operand = node.getOperand();
                        tk.addHash(TokenType.PostfixExpression.ordinal(),node.getOperator().toString(), startLine,basePath,verNum);


                        return true;
                    }

                    public boolean	visit(PrefixExpression node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());

                        String operator = node.getOperator().toString();
                        tk.addHash(TokenType.InfixOperator.ordinal(), operator, startLine,basePath,verNum);
                        ITypeBinding binding = node.getOperand().resolveTypeBinding();

                        if (binding != null) {
                            tk.addHash(TokenType.PrefixExpression.ordinal(), binding.getQualifiedName(), startLine,basePath,verNum);
                        }
                        else {
                            tk.addHash(TokenType.PrefixExpression.ordinal(), startLine,basePath,verNum);
                        }
                        return true;
                    }

                    /*
                    public boolean visit(Name node) {

                        System.out.println("ddd");
                        return true;
                    }*/

                    /*
                     * always together with VariableDeclarationFragment
                     */
        //			public boolean	visit(PrimitiveType node) {
        //
        //				int startLine = unit.getLineNumber(node.getStartPosition());
        //				tk.addHash(47, node.toString(), startLine);
        //				return true;
        //			}

                    public boolean	visit(QualifiedName node) {
                        int startLine = unit.getLineNumber(node.getStartPosition());

                        tk.addHash(TokenType.QualifiedName.ordinal(), node.toString(), startLine,basePath,verNum);

                        return true;
                    }
/*
                    public boolean visit(AbstractTypeDeclaration node) {
                        System.out.println("AbstractTypeDeclaration");
                        return true;
                    }

                    public boolean visit(BodyDeclaration node) {
                        System.out.println("BodyDeclaration node");
                        return true;
                    }

                    public boolean	visit(QualifiedType node) {
                        System.out.println("QualifiedType");

                        int startLine = unit.getLineNumber(node.getStartPosition());

                        System.out.println("QualifiedType at line "+ Integer.toString(startLine));

                        return true;
                    }
*/
                    public boolean	visit(ReturnStatement node) {
                        /*
                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.statementStart(startLine);

                        Expression exp = node.getExpression();
                        if (exp == null) {
                            tk.addHash(48, startLine);
                            return true;
                        }
                        ITypeBinding bind = exp.resolveTypeBinding();
                        if (bind != null) {
                            String s = bind.getQualifiedName();
                            tk.addHash(48,s,startLine);
                        }
                        else {
                            tk.addHash(48, startLine);
                        }
                        */
                        return false;
                    }

                    /*
                    public void endVisit(ReturnStatement node) {
                        tk.statementEnd(scopeLevel);
                    }
                    */

                    public boolean visit(SimpleName node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());

                        String varType = TypeInfo.getMappedType(node.toString(), variableMap);
                        if (varType != null) {
                            //System.out.println(node);
                            tk.addHash(TokenType.SimpleName.ordinal(), varType, startLine,basePath,verNum);
                        } else {
                            tk.addHash(TokenType.SimpleName.ordinal(), node.toString(), startLine,basePath,verNum);
                        }

                        tk.insertSimpleName(node.toString());

                        return true;
                    }

                    /*
                    public boolean	visit(SimpleType node) {
                        System.out.println("ddd  " + node.toString());
                        int startLine = unit.getLineNumber(node.getStartPosition());
                        ITypeBinding typeBinding = node.resolveBinding();

                        if (typeBinding != null) {
                            tk.addHash(49, typeBinding.getQualifiedName(), startLine);
                        }
                        else {
                            tk.addHash(49, node.getName().getFullyQualifiedName(), startLine);
                        }

                        return true;
                    }*/
                    
                    public boolean	visit(SingleMemberAnnotation node) {
                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash(TokenType.SingleMemberAnnotation.ordinal(), node.getTypeName().toString(), startLine,basePath,verNum);				
                        
                        return true;
                    }

                    public boolean	visit(SingleVariableDeclaration node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());

                        IVariableBinding binding = node.resolveBinding();
                        String varName = node.getName().toString();
                        if (binding != null) {
                            String variableType = binding.getType().getName();
                            tk.addHash(TokenType.SingleVariableDeclaration.ordinal(), variableType, startLine,basePath,verNum);

                            TypeInfo typeInfo = new TypeInfo(varName, scopeLevel, variableType);
                            variableMap.add(typeInfo);

                        } else {
                            tk.addHash(TokenType.SingleVariableDeclaration.ordinal(), startLine,basePath,verNum);

                            TypeInfo typeInfo = new TypeInfo(varName, scopeLevel, node.getType().toString());
                            variableMap.add(typeInfo);
                        }
                        return true;
                    }

                    public boolean	visit(StringLiteral node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        String strValue = node.getEscapedValue();
                        strValue = strValue.substring(1,strValue.length() - 1);
                        tk.addHash(TokenType.StringLiteral.ordinal(), strValue, startLine,basePath,verNum);

                        Set<String> termSet = Utilities.extractTermsFromSentence(strValue);
                        for (String word : termSet) {
                            tk.insertSimpleName(word);
                        }

                        return true;
                    }

                    public boolean	visit(SuperConstructorInvocation node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);
                        tk.addHash(TokenType.SuperConstructorInvocation.ordinal(), startLine,basePath,verNum);

                        return true;
                    }

                    public void endVisit(SuperConstructorInvocation node) {
                    	int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.statementEnd(scopeLevel, startLine, basePath,  verNum);
                    }

                    public boolean	visit(SuperFieldAccess node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash(TokenType.SuperFieldAccess.ordinal(), startLine,basePath,verNum);
                        return true;
                    }

                    public boolean	visit(SuperMethodInvocation node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash(TokenType.SuperMethodInvocation.ordinal(), node.getName().toString(), startLine,basePath,verNum);

                        /*
                        if (node.getExpression() != null) {
                            tk.addHash(86, node.getExpression().toString(), startLine);
                        }*/

                        return true;
                    }

                    /*
                    public boolean	visit(SwitchCase node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash(54, startLine);
                        return true;
                    }*/

                    public boolean	visit(SwitchStatement node) {
                        /*

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.statementStart(startLine);

                        tk.addHash(55, startLine);

                        tk.statementEnd(scopeLevel);
                        */
                        return false;
                    }

                    public boolean	visit(SynchronizedStatement node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);

                        tk.addHash(TokenType.SynchronizedStatement.ordinal(), startLine,basePath,verNum);

                        return true;
                    }

                    public boolean	visit(TagElement node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash(TokenType.TagElement.ordinal(), startLine,basePath,verNum);
                        return true;
                    }

                    public boolean	visit(TextElement node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash(TokenType.TextElement.ordinal(), startLine,basePath,verNum);

                        return true;
                    }

                    public boolean	visit(ThisExpression node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash(TokenType.ThisExpression.ordinal(), startLine,basePath,verNum);
                        return true;
                    }

                    public boolean	visit(ThrowStatement node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);
                        tk.addHash(TokenType.ThrowStatement.ordinal(), startLine,basePath,verNum);

                        return true;
                    }

                    public void endVisit(ThrowStatement node) {
                    	int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.statementEnd(scopeLevel, startLine, basePath,  verNum);
                    }

                    public boolean	visit(TryStatement node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);
                        tk.addHash(TokenType.TryStatement.ordinal(), startLine,basePath,verNum);
                        tk.statementEnd(scopeLevel, startLine, basePath,  verNum);

                        return true;
                    }
                    
                    public boolean	visit(TypeDeclaration node) {
                        //int startLine = unit.getLineNumber(node.getStartPosition());
                        //tk.statementStart(startLine);
                        //tk.addHash(62, startLine);
                        scopeLevel = scopeLevel + 1;
                        return true;
                    }

                    public void endVisit(TypeDeclaration node) {
                        
                        clearLocalVariables();

                        scopeLevel = scopeLevel - 1;       
                    }

                    /*
                    public boolean	visit(TypeDeclarationStatement node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.statementStart(startLine);
                        tk.statementEnd(scopeLevel);

                        System.out.println("TypeDeclarationStatement at line "+ Integer.toString(startLine));

                        return true;
                    }*/

                    /*
                    public boolean	visit(TypeLiteral node) {

                        return true;
                    }

                    public boolean	visit(TypeParameter node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());

                        //System.out.println("TypeParameter at line "+ Integer.toString(startLine));

                        return true;
                    }

                    public boolean	visit(UnionType node) {
                        System.out.println("uniontype");
                        return true;
                    }
                    */

                    public boolean visit(VariableDeclarationExpression node) {
                        //System.out.println("VariableDeclarationExpression");
                        int startLine = unit.getLineNumber(node.getStartPosition());

                        Type type = node.getType();
                        tk.addHash(TokenType.VariableDeclarationExpression.ordinal(), type.toString(), startLine,basePath,verNum);

                        List<VariableDeclarationFragment> fragList = node.fragments();
                        for (VariableDeclarationFragment frag : fragList) {
                            TypeInfo typeInfo = new TypeInfo(
                                    frag.getName().toString(), scopeLevel, type.toString());
                            variableMap.add(typeInfo);
                        }

                        //System.out.println("VariableDeclarationExpression at line "+ Integer.toString(startLine));
                        return true;
                    }

                    public boolean visit(VariableDeclarationFragment node) {			   
                        //System.out.println("VariableDeclarationFragment");

                        int startLine = unit.getLineNumber(node.getStartPosition());

                        //System.out.println(node);
                        tk.addHash(TokenType.VariableDeclarationFragment.ordinal(), startLine,basePath,verNum);

                        return true;
                    }
                    
                    public boolean	visit(VariableDeclarationStatement node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);
                        tk.addHash(TokenType.VariableDeclarationStatement.ordinal(), startLine,basePath,verNum);

                        List<VariableDeclarationFragment> fragments = node.fragments();
                        for (VariableDeclarationFragment fragment : fragments) {
                            String varName = fragment.getName().toString();
                            String unresolvedType = node.getType().toString();   
                            TypeInfo typeInfo = new TypeInfo(varName, scopeLevel, unresolvedType);
                            variableMap.add(typeInfo);
                        }

                        return true;
                    }

                    public void endVisit(VariableDeclarationStatement node) {
                    	int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.statementEnd(scopeLevel, startLine, basePath,  verNum);
                    }
                    
                    public boolean	visit(WhileStatement node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);
                        tk.addHash(TokenType.WhileStatement.ordinal(), startLine,basePath,verNum);

                        return true;
                    }

                    /*
                    public void endVisit(WhileStatement node) {
                        int startLine = unit.getLineNumber(node.getStartPosition()+node.getLength());
                        //tk.statementStart(startLine);
                        //tk.addHash(76, startLine);
                        //tk.statementEnd(scopeLevel);
                    }*/

                    public boolean	visit(WildcardType node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);
                        tk.addHash(TokenType.WildcardType.ordinal(), startLine,basePath,verNum);
                        tk.statementEnd(scopeLevel, startLine, basePath,  verNum);
                        return true;
                    }
                    
                });
            } catch (Exception e) {
                System.out.println("Crashed while processing : " + absPath);
                System.out.println("Problem : " + e.toString());
                e.printStackTrace();
                System.exit(0);
            }
            return tk;

        } catch (Exception e) {
            System.out.println("\nError while executing compilation unit : " + e.toString());
            return null;
        }
    }
    
    public static Tokenizer parseAST2Tokens2(String absPath, int minNumLines, boolean debug,String basePath,int verNum) {
        String source = "";
        try {
            source = fileToString(absPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ASTParser parser = ASTParser.newParser(AST.JLS4);

        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        char[] content = source.toCharArray();
        parser.setSource(content);
        parser.setUnitName(absPath);
        Map<String, String> options = JavaCore.getOptions();
        options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_7);
        options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM,
            JavaCore.VERSION_1_7);
        options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_7);
        
        // new String[] {"UTF-8"}
        String[] sources = {};
        String[] classPaths = {};
        parser.setEnvironment(classPaths, sources,
            null, true);
        //parser.setBindingsRecovery(true);
        parser.setResolveBindings(false);
        parser.setCompilerOptions(options);
        parser.setStatementsRecovery(true);

        //System.out.println(absPath);

        try {
            final CompilationUnit unit = (CompilationUnit) parser.createAST(null);
            final AST ast = unit.getAST();

            // Process the main body
            final Tokenizer tk = new Tokenizer(minNumLines, debug);
            try {
                unit.accept(new ASTVisitor() {
                
                    /*
                    // JavaDoc comment that starts with "/** for type declarations"
                    public boolean	visit(AnnotationTypeDeclaration node) {
                        
                        int startLine = unit.getLineNumber(node.getStartPosition());
                        return true;
                    }
                    
                    // JavaDoc comment that starts with "/** for type member declarations"
                    public boolean	visit(AnnotationTypeMemberDeclaration node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        return true;
                    }
                    */

                    public boolean	visit(AnonymousClassDeclaration node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash2(TokenType.AnonymousClassDeclaration.ordinal(), startLine,basePath,verNum);

                        return true;
                    }
                    
                    public boolean	visit(ArrayAccess node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash2(TokenType.ArrayAccess.ordinal(), startLine,basePath,verNum);

                        return true;
                    }

                    public boolean	visit(ArrayCreation node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash2(TokenType.ArrayCreation.ordinal(), startLine,basePath,verNum);
                        return true;
                    }

                    public boolean	visit(ArrayInitializer node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash2(TokenType.ArrayInitializer.ordinal(), startLine,basePath,verNum);

                        return true;
                    }

                    public boolean	visit(ArrayType node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        ITypeBinding binding = node.getElementType().resolveBinding();
                        if (binding != null) {
                            tk.addHash2(TokenType.ArrayType.ordinal(),binding.getQualifiedName(), startLine,basePath,verNum);
                        }
                        else {
                            tk.addHash2(TokenType.ArrayType.ordinal(), startLine,basePath,verNum);
                        }

                        return true;
                    }

                    public boolean	visit(AssertStatement node) {
                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);
                        tk.addHash2(TokenType.AssertStatement.ordinal(), startLine,basePath,verNum);

                        return true;
                    }

                    public void endVisit(AssertStatement node) {
                    	int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.statementEnd2(scopeLevel, startLine, basePath,  verNum);
                    }

                    public boolean	visit(Assignment node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());				
                        String operator = node.getOperator().toString();
                        tk.addHash2(TokenType.Assignment.ordinal(), operator, startLine,basePath,verNum);

                        return true;
                    }
                    
                    public boolean	visit(Block node) {
                        scopeLevel = scopeLevel + 1;
                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.statementEnd2(scopeLevel, startLine, basePath,  verNum);

                        return true;
                    }

                    public void endVisit(Block node) {
                        clearLocalVariables();

                        scopeLevel = scopeLevel - 1;
                    }

                    public boolean	visit(BooleanLiteral node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        if (node.booleanValue()) {
                            tk.addHash2(TokenType.BooleanLiteral.ordinal(), "true", startLine,basePath,verNum);
                        } else {
                            tk.addHash2(TokenType.BooleanLiteral.ordinal(), "false", startLine,basePath,verNum);
                        }
                        return true;
                    }

                    public boolean	visit(BreakStatement node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);
                        tk.addHash2(TokenType.BreakStatement.ordinal(), startLine,basePath,verNum);

                        return true;
                    }

                    public void endVisit(BreakStatement node) {
                    	int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.statementEnd2(scopeLevel, startLine, basePath,  verNum);
                    }

                    public boolean	visit(CastExpression node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash2(TokenType.CastExpression.ordinal(), startLine,basePath,verNum); //reminder
                        return true;
                    }

                    public boolean	visit(CatchClause node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);
                        tk.addHash2(TokenType.CatchClause.ordinal(), startLine,basePath,verNum);

                        return true;
                    }

                    public boolean	visit(CharacterLiteral node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash2(TokenType.CharacterLiteral.ordinal(), node.getEscapedValue(), startLine,basePath,verNum);
                        return true;
                    }

                    public boolean	visit(ClassInstanceCreation node) {

                        scopeLevel = scopeLevel + 1;

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash2(TokenType.ClassInstanceCreation.ordinal(), startLine,basePath,verNum);
                        return true;
                    }

                    public void endVisit(ClassInstanceCreation node) {
                        scopeLevel = scopeLevel - 1;
                    }

                    /*
                    public boolean	visit(CompilationUnit node) {
                        int startLine = unit.getLineNumber(node.getStartPosition());
                        return true;
                    }
                    */

                    public boolean	visit(ConditionalExpression node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash2(TokenType.ConditionalExpression.ordinal(), startLine,basePath,verNum);

                        return true;
                    }

                    public boolean	visit(ConstructorInvocation node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);
                        tk.addHash2(TokenType.SuperConstructorInvocation.ordinal(), startLine,basePath,verNum);

                        return true;
                    }

                    public void endVisit(ConstructorInvocation node) {
                    	int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.statementEnd2(scopeLevel, startLine, basePath,  verNum);
                    }

                    public boolean	visit(ContinueStatement node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);
                        tk.addHash2(TokenType.ContinueStatement.ordinal(), startLine,basePath,verNum);

                        return true;
                    }

                    public void endVisit(ContinueStatement node) {
                    	int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.statementEnd2(scopeLevel, startLine, basePath,  verNum);
                    }

                    public boolean	visit(DoStatement node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());	
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);
                        tk.addHash2(TokenType.DoStatement.ordinal(), startLine,basePath,verNum);

                        return true;
                    }

                    public void endVisit(DoStatement node) {
                    	int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.statementEnd2(scopeLevel, startLine, basePath,  verNum);
                    }

                    /*
                    public boolean	visit(EmptyStatement node) {
                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.statementStart(startLine);

                        tk.addHash(78, startLine);
                        tk.statementEnd(scopeLevel);

                        return true;
                    }*/

                    public boolean	visit(EnhancedForStatement node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);
                        tk.addHash2(TokenType.EnhancedForStatement.ordinal(), startLine,basePath,verNum);

                        return true;
                    }

                    /*
                    public boolean	visit(EnumConstantDeclaration node) {
                        System.out.println("EnumConstantDeclaration");

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        System.out.println("EnumConstantDeclaration at line "+ Integer.toString(startLine));
                        return true;
                    }*/

                    /*
                    public boolean	visit(EnumDeclaration node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        return true;
                    }
                    */

                    public boolean	visit(ExpressionStatement node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);

                        Expression exp = node.getExpression();

                        return true;
                    }

                    public void endVisit(ExpressionStatement node) {
                    	int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.statementEnd2(scopeLevel, startLine, basePath,  verNum);
                    }

                    public boolean	visit(FieldAccess node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        String fieldName = node.getName().toString();
                        tk.addHash2(TokenType.FieldAccess.ordinal(), fieldName, startLine,basePath,verNum);

                        return true;
                    }
                    /*
                     * always together with VariableDeclarationFragment
                     */
                    public boolean	visit(FieldDeclaration node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());

                        Type type = node.getType();
                        ITypeBinding binding = type.resolveBinding();

                        List<VariableDeclarationFragment> fragments = node.fragments();
                        for (VariableDeclarationFragment fragment : fragments) {
                            String name = fragment.getName().toString();
                            if (binding != null) {

                                TypeInfo typeInfo = new TypeInfo(
                                        name, scopeLevel, binding.getName());

                                variableMap.add(typeInfo);
                                /*
                                if (!within_method.isEmpty()) {
                                    // In a method
                                    tk.statementStart(startLine);
                                    tk.addHash(85, binding.getName(), startLine);
                                }
                                */
                            } else {

                                String varType = node.getType().toString();
                                TypeInfo typeInfo = new TypeInfo(
                                        name, scopeLevel, varType);

                                variableMap.add(typeInfo);
                                /*
                                if (!within_method.isEmpty()) {
                                    // In a method
                                    tk.addHash(85, varType, startLine);
                                }
                                */
                            }
                        }
                        return true;
                    }

                    public boolean	visit(ForStatement node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);

                        tk.addHash2(TokenType.ForStatement.ordinal(),startLine,basePath,verNum);

                        return true;
                    }

                    public boolean	visit(IfStatement node) {
                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);
                        ASTNode parent = node.getParent();
                        boolean if_else_happen = false;
                        if (parent != null) {
                            if (parent.getNodeType() == 25) {
                                Statement else_statement = ((IfStatement) parent).getElseStatement();
                                if (else_statement != null) {
                                    if (else_statement.equals(node)) {
                                        if_else_happen = true;
                                    }
                                }
                            }
                        }

                        if (if_else_happen) {
                            tk.addHash2(TokenType.Elseif.ordinal(), startLine,basePath,verNum);
                        } else {
                            tk.addHash2(TokenType.IfStatement.ordinal(),startLine,basePath,verNum);
                        }

                        return true;
                    }
                    
                    public void	endVisit(IfStatement node) {
                        int startLine = unit.getLineNumber(node.getStartPosition()+node.getLength());
                        ASTNode parent = node.getParent();
                        if (parent != null) {
                            if (parent.getNodeType() == 25) {
                                Statement else_statement = ((IfStatement) parent).getElseStatement();
                                if (else_statement != null) {
                                    if (else_statement.equals(node)) {
                                        return;
                                    }
                                }
                            }
                        }
                    }
                    
                    public void	preVisit(ASTNode node) {
                        ASTNode parent = node.getParent();
                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        
                        if (parent != null) {
                            if (parent.getNodeType() == 25 && node.getNodeType() != 25) {
                                Statement else_statement = ((IfStatement) parent).getElseStatement();
                                if (else_statement != null) {
                                    if (else_statement.equals(node)) {
                                        tk.statementStart(startLine, endLine);
                                        tk.addHash2(TokenType.Else.ordinal(), startLine,basePath,verNum);
                                        tk.statementEnd2(scopeLevel, startLine, basePath,  verNum);
                                    }
                                }
                            }
                        }
                    }
                    
                    public boolean	visit(InfixExpression node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        String operator = node.getOperator().toString();
                        tk.addHash2(TokenType.InfixExpression.ordinal(), operator,startLine,basePath,verNum);

                        return true;
                    }

                    /*
                    public boolean	visit(Initializer node) {
                        System.out.println("Initializer");

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash(28,startLine);

                        return true;
                    }*/

                    public boolean	visit(InstanceofExpression node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash2(TokenType.InstanceofExpression.ordinal(),startLine,basePath,verNum);
                        return true;
                    }

                    /* Comments */
        //			public boolean	visit(Javadoc node) {
        //
        //				int startLine = unit.getLineNumber(node.getStartPosition());
        //
        //				System.out.println("Javadoc at line "+ Integer.toString(startLine));
        //
        //				return true;
        //			}

                    public boolean	visit(LabeledStatement node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);
                        tk.addHash2(TokenType.LabeledStatement.ordinal(), startLine,basePath,verNum);
                        tk.statementEnd2(scopeLevel, startLine, basePath,  verNum);

                        return true;
                    }
                    
                    /*
                    public boolean visit(Annotation node) {

                        System.out.println("annotation node");

                        return true;

                    }

                    public boolean	visit(MarkerAnnotation node) {

                        return false;
                        //int startLine = unit.getLineNumber(node.getStartPosition());
                        //tk.addHash(31, node.getTypeName().getFullyQualifiedName(),startLine);

                        //return true;
                    }
                    */

                    public boolean	visit(MemberRef node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash2(TokenType.MemberRef.ordinal(), node.resolveBinding().getName(), startLine,basePath,verNum);

                        return true;
                    }

                    public boolean	visit(MemberValuePair node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash2(TokenType.MemberValuePair.ordinal(), startLine,basePath,verNum);

                        return true;
                    }

                    public boolean	visit(MethodDeclaration node) {
                        tk.inMethod = true;

                        // Get Line number
                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        
                        tk.methodStart(node.getName().toString(), startLine);

                        tk.statementStart(startLine, endLine);

                        tk.addHash2(TokenType.MethodDeclaration.ordinal(), startLine,basePath,verNum);

                        return true;
                    }
                    
                    public void endVisit(MethodDeclaration node) {
                        tk.inMethod = false;

                        int startLine = unit.getLineNumber(node.getStartPosition()+node.getLength());
                        //tk.statementStart(startLine);
                        //tk.addHash(75, startLine);
                        //tk.statementEnd(scopeLevel);

                        //IMethodBinding methodBinding = node.resolveBinding();
                        tk.methodEnd(node.getName().toString(), startLine);
                        //System.out.println(node.getName().toString());
                    }

                    public boolean	visit(MethodInvocation node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash2(TokenType.MethodInvocation.ordinal(), startLine,basePath,verNum);
                        tk.hasMethodInvocation();

                        return true;
                    }

                    /*
                    public boolean	visit(MethodRef node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());

                        System.out.println("MethodRef at line "+ Integer.toString(startLine));

                        return true;
                    }

                    public boolean	visit(MethodRefParameter node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());

                        System.out.println("MethodRefParameter at line "+ Integer.toString(startLine));

                        return true;
                    }*/


                    /*	public, protected, private, static, abstract, final, native, synchronized, transient
                     *  volatile, strictfp
                     */
                    /*
                    public boolean	visit(Modifier node) {
                        System.out.println("modifier node");
                        return true;
                    }
                    */
                 

                    /*
                    public boolean	visit(NormalAnnotation node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        System.out.println("Initializer at line "+ Integer.toString(startLine));
                        return true;
                    }
                    */

                    public boolean	visit(NullLiteral node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash2(TokenType.NullLiteral.ordinal(), startLine,basePath,verNum);
                        return true;
                    }

                    public boolean	visit(NumberLiteral node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash2(TokenType.NumberLiteral.ordinal(), startLine,basePath,verNum);

                        //System.out.println("NumberLiteral at line "+ Integer.toString(startLine));

                        return true;
                    }

                    public boolean visit(ImportDeclaration node) {
                        return false;
                    }

                    public boolean	visit(PackageDeclaration node) {
                        return false;
        //
        //				//int startLine = unit.getLineNumber(node.getStartPosition());
        //
        ////				SimpleName name = (SimpleName) node.getName();
        ////				System.out.println("PackageDeclaration with name of "+name+" at line "+ unit.getLineNumber(name.getStartPosition()));
        //				System.out.println("PackageDeclaration with name of ");
        //
        //				return true;
                    }

                    /*
                    public boolean	visit(ParameterizedType node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash(43, startLine);

                        return true;
                    }*/

                    public boolean	visit(ParenthesizedExpression node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());				
                        tk.addHash2(TokenType.ParenthesizedExpression.ordinal(), startLine,basePath,verNum);
                        return true;
                    } 

                    public boolean	visit(PostfixExpression node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        Expression operand = node.getOperand();
                        tk.addHash2(TokenType.PostfixExpression.ordinal(),node.getOperator().toString(), startLine,basePath,verNum);


                        return true;
                    }

                    public boolean	visit(PrefixExpression node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());

                        String operator = node.getOperator().toString();
                        tk.addHash2(TokenType.InfixOperator.ordinal(), operator, startLine,basePath,verNum);
                        ITypeBinding binding = node.getOperand().resolveTypeBinding();

                        if (binding != null) {
                            tk.addHash2(TokenType.PrefixExpression.ordinal(), binding.getQualifiedName(), startLine,basePath,verNum);
                        }
                        else {
                            tk.addHash2(TokenType.PrefixExpression.ordinal(), startLine,basePath,verNum);
                        }
                        return true;
                    }

                    /*
                    public boolean visit(Name node) {

                        System.out.println("ddd");
                        return true;
                    }*/

                    /*
                     * always together with VariableDeclarationFragment
                     */
        //			public boolean	visit(PrimitiveType node) {
        //
        //				int startLine = unit.getLineNumber(node.getStartPosition());
        //				tk.addHash(47, node.toString(), startLine);
        //				return true;
        //			}

                    public boolean	visit(QualifiedName node) {
                        int startLine = unit.getLineNumber(node.getStartPosition());

                        tk.addHash2(TokenType.QualifiedName.ordinal(), node.toString(), startLine,basePath,verNum);

                        return true;
                    }
/*
                    public boolean visit(AbstractTypeDeclaration node) {
                        System.out.println("AbstractTypeDeclaration");
                        return true;
                    }

                    public boolean visit(BodyDeclaration node) {
                        System.out.println("BodyDeclaration node");
                        return true;
                    }

                    public boolean	visit(QualifiedType node) {
                        System.out.println("QualifiedType");

                        int startLine = unit.getLineNumber(node.getStartPosition());

                        System.out.println("QualifiedType at line "+ Integer.toString(startLine));

                        return true;
                    }
*/
                    public boolean	visit(ReturnStatement node) {
                        /*
                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.statementStart(startLine);

                        Expression exp = node.getExpression();
                        if (exp == null) {
                            tk.addHash(48, startLine);
                            return true;
                        }
                        ITypeBinding bind = exp.resolveTypeBinding();
                        if (bind != null) {
                            String s = bind.getQualifiedName();
                            tk.addHash(48,s,startLine);
                        }
                        else {
                            tk.addHash(48, startLine);
                        }
                        */
                        return false;
                    }

                    /*
                    public void endVisit(ReturnStatement node) {
                        tk.statementEnd(scopeLevel);
                    }
                    */

                    public boolean visit(SimpleName node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());

                        String varType = TypeInfo.getMappedType(node.toString(), variableMap);
                        if (varType != null) {
                            //System.out.println(node);
                            tk.addHash2(TokenType.SimpleName.ordinal(), varType, startLine,basePath,verNum);
                        } else {
                            tk.addHash2(TokenType.SimpleName.ordinal(), node.toString(), startLine,basePath,verNum);
                        }

                        tk.insertSimpleName(node.toString());

                        return true;
                    }

                    /*
                    public boolean	visit(SimpleType node) {
                        System.out.println("ddd  " + node.toString());
                        int startLine = unit.getLineNumber(node.getStartPosition());
                        ITypeBinding typeBinding = node.resolveBinding();

                        if (typeBinding != null) {
                            tk.addHash(49, typeBinding.getQualifiedName(), startLine);
                        }
                        else {
                            tk.addHash(49, node.getName().getFullyQualifiedName(), startLine);
                        }

                        return true;
                    }*/
                    
                    public boolean	visit(SingleMemberAnnotation node) {
                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash2(TokenType.SingleMemberAnnotation.ordinal(), node.getTypeName().toString(), startLine,basePath,verNum);				
                        
                        return true;
                    }

                    public boolean	visit(SingleVariableDeclaration node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());

                        IVariableBinding binding = node.resolveBinding();
                        String varName = node.getName().toString();
                        if (binding != null) {
                            String variableType = binding.getType().getName();
                            tk.addHash2(TokenType.SingleVariableDeclaration.ordinal(), variableType, startLine,basePath,verNum);

                            TypeInfo typeInfo = new TypeInfo(varName, scopeLevel, variableType);
                            variableMap.add(typeInfo);

                        } else {
                            tk.addHash2(TokenType.SingleVariableDeclaration.ordinal(), startLine,basePath,verNum);

                            TypeInfo typeInfo = new TypeInfo(varName, scopeLevel, node.getType().toString());
                            variableMap.add(typeInfo);
                        }
                        return true;
                    }

                    public boolean	visit(StringLiteral node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        String strValue = node.getEscapedValue();
                        strValue = strValue.substring(1,strValue.length() - 1);
                        tk.addHash2(TokenType.StringLiteral.ordinal(), strValue, startLine,basePath,verNum);

                        Set<String> termSet = Utilities.extractTermsFromSentence(strValue);
                        for (String word : termSet) {
                            tk.insertSimpleName(word);
                        }

                        return true;
                    }

                    public boolean	visit(SuperConstructorInvocation node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);
                        tk.addHash2(TokenType.SuperConstructorInvocation.ordinal(), startLine,basePath,verNum);

                        return true;
                    }

                    public void endVisit(SuperConstructorInvocation node) {
                    	int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.statementEnd2(scopeLevel, startLine, basePath,  verNum);
                    }

                    public boolean	visit(SuperFieldAccess node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash2(TokenType.SuperFieldAccess.ordinal(), startLine,basePath,verNum);
                        return true;
                    }

                    public boolean	visit(SuperMethodInvocation node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash2(TokenType.SuperMethodInvocation.ordinal(), node.getName().toString(), startLine,basePath,verNum);

                        /*
                        if (node.getExpression() != null) {
                            tk.addHash(86, node.getExpression().toString(), startLine);
                        }*/

                        return true;
                    }

                    /*
                    public boolean	visit(SwitchCase node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash(54, startLine);
                        return true;
                    }*/

                    public boolean	visit(SwitchStatement node) {
                        /*

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.statementStart(startLine);

                        tk.addHash(55, startLine);

                        tk.statementEnd(scopeLevel);
                        */
                        return false;
                    }

                    public boolean	visit(SynchronizedStatement node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);

                        tk.addHash2(TokenType.SynchronizedStatement.ordinal(), startLine,basePath,verNum);

                        return true;
                    }

                    public boolean	visit(TagElement node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash2(TokenType.TagElement.ordinal(), startLine,basePath,verNum);
                        return true;
                    }

                    public boolean	visit(TextElement node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash2(TokenType.TextElement.ordinal(), startLine,basePath,verNum);

                        return true;
                    }

                    public boolean	visit(ThisExpression node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.addHash2(TokenType.ThisExpression.ordinal(), startLine,basePath,verNum);
                        return true;
                    }

                    public boolean	visit(ThrowStatement node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);
                        tk.addHash2(TokenType.ThrowStatement.ordinal(), startLine,basePath,verNum);

                        return true;
                    }

                    public void endVisit(ThrowStatement node) {
                    	int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.statementEnd2(scopeLevel, startLine, basePath,  verNum);
                    }

                    public boolean	visit(TryStatement node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);
                        tk.addHash2(TokenType.TryStatement.ordinal(), startLine,basePath,verNum);
                        tk.statementEnd2(scopeLevel, startLine, basePath,  verNum);

                        return true;
                    }
                    
                    public boolean	visit(TypeDeclaration node) {
                        //int startLine = unit.getLineNumber(node.getStartPosition());
                        //tk.statementStart(startLine);
                        //tk.addHash(62, startLine);
                        scopeLevel = scopeLevel + 1;
                        return true;
                    }

                    public void endVisit(TypeDeclaration node) {
                        
                        clearLocalVariables();

                        scopeLevel = scopeLevel - 1;       
                    }

                    /*
                    public boolean	visit(TypeDeclarationStatement node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.statementStart(startLine);
                        tk.statementEnd(scopeLevel);

                        System.out.println("TypeDeclarationStatement at line "+ Integer.toString(startLine));

                        return true;
                    }*/

                    /*
                    public boolean	visit(TypeLiteral node) {

                        return true;
                    }

                    public boolean	visit(TypeParameter node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());

                        //System.out.println("TypeParameter at line "+ Integer.toString(startLine));

                        return true;
                    }

                    public boolean	visit(UnionType node) {
                        System.out.println("uniontype");
                        return true;
                    }
                    */

                    public boolean visit(VariableDeclarationExpression node) {
                        //System.out.println("VariableDeclarationExpression");
                        int startLine = unit.getLineNumber(node.getStartPosition());

                        Type type = node.getType();
                        tk.addHash2(TokenType.VariableDeclarationExpression.ordinal(), type.toString(), startLine,basePath,verNum);

                        List<VariableDeclarationFragment> fragList = node.fragments();
                        for (VariableDeclarationFragment frag : fragList) {
                            TypeInfo typeInfo = new TypeInfo(
                                    frag.getName().toString(), scopeLevel, type.toString());
                            variableMap.add(typeInfo);
                        }

                        //System.out.println("VariableDeclarationExpression at line "+ Integer.toString(startLine));
                        return true;
                    }

                    public boolean visit(VariableDeclarationFragment node) {			   
                        //System.out.println("VariableDeclarationFragment");

                        int startLine = unit.getLineNumber(node.getStartPosition());

                        //System.out.println(node);
                        tk.addHash2(TokenType.VariableDeclarationFragment.ordinal(), startLine,basePath,verNum);

                        return true;
                    }
                    
                    public boolean	visit(VariableDeclarationStatement node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);
                        tk.addHash2(TokenType.VariableDeclarationStatement.ordinal(), startLine,basePath,verNum);

                        List<VariableDeclarationFragment> fragments = node.fragments();
                        for (VariableDeclarationFragment fragment : fragments) {
                            String varName = fragment.getName().toString();
                            String unresolvedType = node.getType().toString();   
                            TypeInfo typeInfo = new TypeInfo(varName, scopeLevel, unresolvedType);
                            variableMap.add(typeInfo);
                        }

                        return true;
                    }

                    public void endVisit(VariableDeclarationStatement node) {
                    	int startLine = unit.getLineNumber(node.getStartPosition());
                        tk.statementEnd2(scopeLevel, startLine, basePath,  verNum);
                    }
                    
                    public boolean	visit(WhileStatement node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);
                        tk.addHash2(TokenType.WhileStatement.ordinal(), startLine,basePath,verNum);

                        return true;
                    }

                    /*
                    public void endVisit(WhileStatement node) {
                        int startLine = unit.getLineNumber(node.getStartPosition()+node.getLength());
                        //tk.statementStart(startLine);
                        //tk.addHash(76, startLine);
                        //tk.statementEnd(scopeLevel);
                    }*/

                    public boolean	visit(WildcardType node) {

                        int startLine = unit.getLineNumber(node.getStartPosition());
                        int endLine = unit.getLineNumber(node.getStartPosition() + node.getLength());
                        tk.statementStart(startLine, endLine);
                        tk.addHash2(TokenType.WildcardType.ordinal(), startLine,basePath,verNum);
                        tk.statementEnd2(scopeLevel, startLine, basePath,  verNum);
                        return true;
                    }
                    
                });
            } catch (Exception e) {
                System.out.println("Crashed while processing : " + absPath);
                System.out.println("Problem : " + e.toString());
                e.printStackTrace();
                System.exit(0);
            }
            return tk;

        } catch (Exception e) {
            System.out.println("\nError while executing compilation unit : " + e.toString());
            return null;
        }
    }
}
