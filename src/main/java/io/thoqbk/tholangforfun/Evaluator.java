package io.thoqbk.tholangforfun;

import java.util.List;

import io.thoqbk.tholangforfun.ast.Program;
import io.thoqbk.tholangforfun.ast.expressions.Bool;
import io.thoqbk.tholangforfun.ast.expressions.Call;
import io.thoqbk.tholangforfun.ast.expressions.Expression;
import io.thoqbk.tholangforfun.ast.expressions.Function;
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
import io.thoqbk.tholangforfun.eval.FunctionResult;
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
            EvalResult result = evalStatement(statement, env);
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

    private EvalResult evalStatement(Statement statement, Env env) {
        if (statement.is(ExpressionStm.class)) {
            return evalExpression(statement.as(ExpressionStm.class).getExpression(), env);
        } else if (statement.is(If.class)) {
            return evalIf(statement.as(If.class), env);
        } else if (statement.is(Block.class)) {
            return evalBlock(statement.as(Block.class), env);
        } else if (statement.is(Return.class)) {
            return new ReturnResult(evalExpression(statement.as(Return.class).getValue(), env));
        } else if (statement.is(Let.class)) {
            Let letStm = statement.as(Let.class);
            env.setVariable(letStm.getVariableName(), evalExpression(letStm.getExpression(), env));
            return NO_RESULT;
        }
        throw new EvalException("Unknown statement " + statement);
    }

    private EvalResult evalIf(If ifStm, Env env) {
        if (isTruthy(evalExpression(ifStm.getCondition(), env))) {
            return evalStatement(ifStm.getIfBody(), env);
        }
        if (ifStm.getElseBody() != null) {
            return evalStatement(ifStm.getElseBody(), env);
        }
        return NULL_RESULT;
    }

    private EvalResult evalBlock(Block block, Env env) {
        EvalResult retVal = NULL_RESULT;
        for (Statement stm : block.getStatements()) {
            EvalResult result = evalStatement(stm, env);
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

    private EvalResult evalExpression(Expression expression, Env env) {
        if (expression.is(Int.class)) {
            return new IntResult(expression.as(Int.class).getValue());
        } else if (expression.is(Bool.class)) {
            return new BoolResult(expression.as(Bool.class).getValue());
        } else if (expression.is(Prefix.class)) {
            return evalPrefix(expression.as(Prefix.class), env);
        } else if (expression.is(Infix.class)) {
            return evalInfix(expression.as(Infix.class), env);
        } else if (expression.is(Identifier.class)) {
            return env.getVariable(expression.as(Identifier.class).getToken().getLiteral());
        } else if (expression.is(Function.class)) {
            Function fn = expression.as(Function.class);
            return new FunctionResult(fn.getParams().stream().map(param -> param.getToken().getLiteral()).toList(),
                    fn.getBody());
        } else if (expression.is(Call.class)) {
            return evalFunctionCall(expression.as(Call.class), env);
        }
        throw new EvalException("Unknown expression " + expression);
    }

    private EvalResult evalPrefix(Prefix prefix, Env env) {
        EvalResult base = evalExpression(prefix.getRight(), env);
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

    private EvalResult evalInfix(Infix infix, Env env) {
        EvalResult left = evalExpression(infix.getLeft(), env);
        EvalResult right = evalExpression(infix.getRight(), env);
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

    private EvalResult evalFunctionCall(Call call, Env parent) {
        Env env = new Env(parent);
        FunctionResult fn = evalExpression(call.getFunction(), env).as(FunctionResult.class);
        for (int idx = 0; idx < fn.getParams().size(); idx++) {
            String paramName = fn.getParams().get(idx);
            EvalResult value = call.getArgs().size() > idx ? evalExpression(call.getArgs().get(idx), env) : NULL_RESULT;
            env.setVariable(paramName, value);
        }
        EvalResult retVal = evalStatement(fn.getBody(), env);
        if (retVal.is(ReturnResult.class)) {
            return retVal.as(ReturnResult.class).getValue();
        }
        return retVal;
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
