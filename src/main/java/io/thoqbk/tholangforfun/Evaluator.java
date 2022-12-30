package io.thoqbk.tholangforfun;

import java.util.List;

import io.thoqbk.tholangforfun.ast.Program;
import io.thoqbk.tholangforfun.ast.expressions.Bool;
import io.thoqbk.tholangforfun.ast.expressions.Expression;
import io.thoqbk.tholangforfun.ast.expressions.Identifier;
import io.thoqbk.tholangforfun.ast.expressions.Infix;
import io.thoqbk.tholangforfun.ast.expressions.Int;
import io.thoqbk.tholangforfun.ast.expressions.Prefix;
import io.thoqbk.tholangforfun.ast.statements.Block;
import io.thoqbk.tholangforfun.ast.statements.ExpressionStm;
import io.thoqbk.tholangforfun.ast.statements.If;
import io.thoqbk.tholangforfun.ast.statements.Let;
import io.thoqbk.tholangforfun.ast.statements.Return;
import io.thoqbk.tholangforfun.ast.statements.Statement;
import io.thoqbk.tholangforfun.eval.BoolResult;
import io.thoqbk.tholangforfun.eval.Env;
import io.thoqbk.tholangforfun.eval.EvalResult;
import io.thoqbk.tholangforfun.eval.IntResult;
import io.thoqbk.tholangforfun.eval.NoResult;
import io.thoqbk.tholangforfun.eval.NullResult;
import io.thoqbk.tholangforfun.eval.ReturnResult;
import io.thoqbk.tholangforfun.exceptions.EvalException;

public class Evaluator {
    private static final EvalResult NULL_RESULT = new NullResult();
    private static final EvalResult NO_RESULT = new NoResult();

    public EvalResult eval(Program program) {
        List<Statement> statements = program.getStatements();
        EvalResult retVal = NULL_RESULT;
        Env env = new Env();
        for (Statement statement : statements) {
            EvalResult result = eval(statement, env);
            if (result.is(NoResult.class)) {
                continue;
            }
            retVal = result;
            if (retVal.is(ReturnResult.class)) {
                break;
            }
        }
        return retVal;
    }

    private EvalResult eval(Statement statement, Env env) {
        if (statement.is(ExpressionStm.class)) {
            return eval(statement.as(ExpressionStm.class), env);
        } else if (statement.is(If.class)) {
            return eval(statement.as(If.class), env);
        } else if (statement.is(Block.class)) {
            return eval(statement.as(Block.class), env);
        } else if (statement.is(Return.class)) {
            return eval(statement.as(Return.class), env);
        } else if (statement.is(Let.class)) {
            return eval(statement.as(Let.class), env);
        }
        throw new EvalException("Unknown statement " + statement);
    }

    private EvalResult eval(ExpressionStm expressionStm, Env env) {
        return eval(expressionStm.getExpression(), env);
    }

    private EvalResult eval(If ifStm, Env env) {
        return isTruthy(eval(ifStm.getCondition(), env)) ? eval(ifStm.getIfBody(), env)
                : eval(ifStm.getElseBody(), env);
    }

    private EvalResult eval(Block block, Env env) {
        EvalResult retVal = NULL_RESULT;
        if (block == null) {
            return retVal;
        }
        for (Statement stm : block.getStatements()) {
            EvalResult result = eval(stm, env);
            if (result.is(NoResult.class)) {
                continue;
            }
            retVal = result;
            if (result.is(ReturnResult.class)) {
                break;
            }
        }
        return retVal;
    }

    private EvalResult eval(Return returnStm, Env env) {
        return new ReturnResult(eval(returnStm.getValue(), env));
    }

    private EvalResult eval(Let letStm, Env env) {
        env.setVariable(letStm.getVariableName(), eval(letStm.getExpression(), env));
        return NO_RESULT;
    }

    private EvalResult eval(Expression expression, Env env) {
        if (expression.is(Int.class)) {
            return new IntResult(expression.as(Int.class).getValue());
        } else if (expression.is(Bool.class)) {
            return new BoolResult(expression.as(Bool.class).getValue());
        } else if (expression.is(Prefix.class)) {
            return eval(expression.as(Prefix.class), env);
        } else if (expression.is(Infix.class)) {
            return eval(expression.as(Infix.class), env);
        } else if (expression.is(Identifier.class)) {
            return env.getVariable(expression.as(Identifier.class).getToken().getLiteral());
        }
        throw new EvalException("Unknown expression " + expression);
    }

    private EvalResult eval(Prefix prefix, Env env) {
        EvalResult base = eval(prefix.getRight(), env);
        switch (prefix.getToken().getType()) {
            case MINUS: {
                return new IntResult(-base.as(IntResult.class).getValue());
            }
            case BANG: {
                boolean truthyIntCase = base != null && base.is(IntResult.class)
                        && base.as(IntResult.class).getValue() == 0;
                boolean truthyBoolCase = base != null && base.is(BoolResult.class)
                        && !base.as(BoolResult.class).getValue();
                return new BoolResult(base == null || truthyIntCase || truthyBoolCase);
            }
            default: {
                throw new EvalException("Invalid prefix operator '" + prefix.getToken().getType() + "'");
            }
        }
    }

    private EvalResult eval(Infix infix, Env env) {
        EvalResult left = eval(infix.getLeft(), env);
        EvalResult right = eval(infix.getRight(), env);
        switch (infix.getToken().getType()) {
            case PLUS: {
                return new IntResult(
                        left.as(IntResult.class).getValue() + right.as(IntResult.class).getValue());
            }
            case MINUS: {
                return new IntResult(
                        left.as(IntResult.class).getValue() - right.as(IntResult.class).getValue());
            }
            case ASTERISK: {
                return new IntResult(
                        left.as(IntResult.class).getValue() * right.as(IntResult.class).getValue());
            }
            case SLASH: {
                return new IntResult(
                        left.as(IntResult.class).getValue() / right.as(IntResult.class).getValue());
            }
            case GT: {
                return new BoolResult(left.as(IntResult.class).getValue() > right.as(IntResult.class).getValue());
            }
            case LT: {
                return new BoolResult(left.as(IntResult.class).getValue() < right.as(IntResult.class).getValue());
            }
            case EQ: {
                return evalEqual(left, right);
            }
            case NOT_EQ: {
                return new BoolResult(!evalEqual(left, right).as(BoolResult.class).getValue());
            }
            default: {
                throw new EvalException("Invalid infix operator '" + infix.getToken().getType() + "'");
            }
        }
    }

    private EvalResult evalEqual(EvalResult left, EvalResult right) {
        boolean intCase = left.is(IntResult.class) && right.is(IntResult.class)
                && left.as(IntResult.class).getValue() == right.as(IntResult.class).getValue();
        boolean boolCase = left.is(BoolResult.class) && right.is(BoolResult.class)
                && left.as(BoolResult.class).getValue() == right.as(BoolResult.class).getValue();
        return new BoolResult(intCase || boolCase);
    }

    private boolean isTruthy(EvalResult result) {
        boolean boolCase = result != null && result.is(BoolResult.class) && result.as(BoolResult.class).getValue();
        boolean intCase = result != null && result.is(IntResult.class) && result.as(IntResult.class).getValue() != 0;
        return boolCase || intCase;
    }
}
