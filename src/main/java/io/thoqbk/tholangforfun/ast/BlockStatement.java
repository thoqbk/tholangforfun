package io.thoqbk.tholangforfun.ast;

import java.util.ArrayList;
import java.util.List;

import io.thoqbk.tholangforfun.Token;

public class BlockStatement extends Statement {
    private List<Statement> statements = new ArrayList<>();

    public BlockStatement(Token token) {
        super(token);
    }

    public List<Statement> getStatements() {
        return statements;
    }

}
