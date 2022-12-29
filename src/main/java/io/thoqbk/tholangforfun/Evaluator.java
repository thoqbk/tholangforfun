package io.thoqbk.tholangforfun;

import java.util.List;

import io.thoqbk.tholangforfun.ast.Program;
import io.thoqbk.tholangforfun.ast.expressions.Expression;
import io.thoqbk.tholangforfun.ast.expressions.Int;
import io.thoqbk.tholangforfun.ast.statements.ExpressionStm;
import io.thoqbk.tholangforfun.ast.statements.Statement;
import io.thoqbk.tholangforfun.eval.EvalResult;
import io.thoqbk.tholangforfun.eval.IntResult;

public class Evaluator {
    public EvalResult eval(Program program) {
        List<Statement> statements = program.getStatements();
        EvalResult retVal = null;
        for (Statement statement : statements) {
            retVal = eval(statement);
        }
        return retVal;
    }

    private EvalResult eval(Statement statement) {
        if (statement instanceof ExpressionStm) {
            return eval(statement.as(ExpressionStm.class));
        }
        return null;
    }

    private EvalResult eval(ExpressionStm expressionStm) {
        Expression expression = expressionStm.getExpression();
        if (expression instanceof Int) {
            return new IntResult(expression.as(Int.class).getValue());
        }
        return null;
    }
}
