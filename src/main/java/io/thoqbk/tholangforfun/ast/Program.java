package io.thoqbk.tholangforfun.ast;

import java.util.List;

import io.thoqbk.tholangforfun.ast.statements.Statement;

public class Program {
    private List<Statement> statements;

    public void setStatments(List<Statement> statements) {
        this.statements = statements;
    }

    public List<Statement> getStatements() {
        return statements;
    }
}
