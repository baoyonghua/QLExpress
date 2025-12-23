package com.alibaba.qlexpress4.aparser;

public abstract class ScopeStackVisitor extends com.alibaba.qlexpress4.aparser.QLParserBaseVisitor<Void> {
    
    private ExistStack existStack;
    
    protected ScopeStackVisitor(ExistStack existStack) {
        this.existStack = existStack;
    }
    
    // scope
    @Override
    public Void visitBlockExpr(com.alibaba.qlexpress4.aparser.QLParser.BlockExprContext ctx) {
        push();
        super.visitBlockExpr(ctx);
        pop();
        return null;
    }
    
    @Override
    public Void visitQlIf(com.alibaba.qlexpress4.aparser.QLParser.QlIfContext qlIfContext) {
        qlIfContext.condition.accept(this);
        
        push();
        qlIfContext.thenBody().accept(this);
        pop();
        
        com.alibaba.qlexpress4.aparser.QLParser.ElseBodyContext elseBodyContext = qlIfContext.elseBody();
        if (elseBodyContext != null) {
            push();
            elseBodyContext.accept(this);
            pop();
        }
        
        return null;
    }
    
    @Override
    public Void visitTryCatchExpr(com.alibaba.qlexpress4.aparser.QLParser.TryCatchExprContext ctx) {
        com.alibaba.qlexpress4.aparser.QLParser.BlockStatementsContext blockStatementsContext = ctx.blockStatements();
        if (blockStatementsContext != null) {
            push();
            blockStatementsContext.accept(this);
            pop();
        }
        
        com.alibaba.qlexpress4.aparser.QLParser.TryCatchesContext tryCatchesContext = ctx.tryCatches();
        if (tryCatchesContext != null) {
            tryCatchesContext.accept(this);
        }
        
        com.alibaba.qlexpress4.aparser.QLParser.TryFinallyContext tryFinallyContext = ctx.tryFinally();
        if (tryFinallyContext != null) {
            push();
            tryFinallyContext.accept(this);
            pop();
        }
        
        return null;
    }
    
    @Override
    public Void visitTryCatch(com.alibaba.qlexpress4.aparser.QLParser.TryCatchContext ctx) {
        push();
        super.visitTryCatch(ctx);
        pop();
        return null;
    }
    
    @Override
    public Void visitFunctionStatement(com.alibaba.qlexpress4.aparser.QLParser.FunctionStatementContext ctx) {
        ctx.varId().accept(this);
        com.alibaba.qlexpress4.aparser.QLParser.FormalOrInferredParameterListContext paramList = ctx.formalOrInferredParameterList();
        if (paramList != null) {
            paramList.accept(this);
        }
        
        com.alibaba.qlexpress4.aparser.QLParser.BlockStatementsContext functionBlockStatements = ctx.blockStatements();
        if (functionBlockStatements != null) {
            push();
            functionBlockStatements.accept(this);
            pop();
        }
        
        return null;
    }
    
    public void push() {
        this.existStack = this.existStack.push();
    }
    
    public void pop() {
        this.existStack = this.existStack.pop();
    }
    
    public ExistStack getStack() {
        return existStack;
    }
}
