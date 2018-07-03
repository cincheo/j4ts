package java.lang;

import def.js.Function;

import java.util.Random;

import static def.js.Globals.eval;
import static jsweet.util.Lang.*;

public final class Math {
    public static final double E = 2.718281828459045D;
    public static final double PI = def.js.Math.PI;

    private Math() {
    }
    
    public static Function getMathFunction(String funName) {
        return object(eval("window.Math")).$get(funName);
    }
    
    public static double sin(double arg) {
        return def.js.Math.sin(arg);
    }

    public static double cos(double arg) {
        return def.js.Math.cos(arg);
    }

    public static double tan(double arg) {
        return def.js.Math.tan(arg);
    }

    public static double asin(double arg) {
        return def.js.Math.asin(arg);
    }

    public static double acos(double arg) {
        return def.js.Math.acos(arg);
    }

    public static double atan(double arg) {
        return def.js.Math.atan(arg);
    }

    public static double toRadians(double arg) {
        return arg / 180.0D * PI;
    }

    public static double toDegrees(double arg) {
        return arg * 180.0D / PI;
    }

    public static double exp(double arg) {
        return def.js.Math.exp(arg);
    }

    public static double log(double arg) {
        return def.js.Math.log(arg);
    }

    public static double log10(double arg) {
        return log(arg) / log(10);
    }

    public static double sqrt(double arg) {
        return def.js.Math.sqrt(arg);
    }

    public static double cbrt(double arg) {
        return (double) getMathFunction("cbrt").$apply(object(arg));
    }

    public static double IEEEremainder(double dividend, double divisor) {
        return dividend - (divisor * round(dividend / divisor));
    }

    public static double ceil(double arg) {
        return def.js.Math.ceil(arg);
    }

    public static double floor(double arg) {
        return def.js.Math.floor(arg);
    }


    public static double atan2(double arg, double arg2) {
        return def.js.Math.atan2(arg, arg2);
    }

    public static double pow(double arg, double arg2) {
        return def.js.Math.pow(arg, arg2);
    }

    public static int round(float arg) {
        return (arg % 1.0 + 1.0) % 1.0 >= 0.5 ? (int) arg + 1 : (int) arg;
    }

    public static long round(double arg) {
        return (arg % 1.0 + 1.0) % 1.0 >= 0.5 ? (int) arg + 1 : (int) arg;
    }

    public static double random() {
        return RandomNumberGeneratorHolder.randomNumberGenerator.nextDouble();
    }

    public static int addExact(int arg, int arg2) {
        int added = arg + arg2;
        if (((arg ^ added) & (arg2 ^ added)) < 0) {
            throw new ArithmeticException("integer overflow");
        } else {
            return added;
        }
    }

    public static long addExact(long arg, long arg2) {
        long added = arg + arg2;
        if (((arg ^ added) & (arg2 ^ added)) < 0L) {
            throw new ArithmeticException("long overflow");
        } else {
            return added;
        }
    }

    public static int subtractExact(int arg, int arg2) {
        int subtracted = arg - arg2;
        if (((arg ^ arg2) & (arg ^ subtracted)) < 0) {
            throw new ArithmeticException("integer overflow");
        } else {
            return subtracted;
        }
    }

    public static long subtractExact(long arg, long arg2) {
        long subtracted = arg - arg2;
        if (((arg ^ arg2) & (arg ^ subtracted)) < 0L) {
            throw new ArithmeticException("long overflow");
        } else {
            return subtracted;
        }
    }

    public static int multiplyExact(int arg, int arg2) {
        long multiplied = (long) arg * (long) arg2;
        if ((long) ((int) multiplied) != multiplied) {
            throw new ArithmeticException("integer overflow");
        } else {
            return (int) multiplied;
        }
    }

    public static long multiplyExact(long arg, long arg2) {
        long multiplied = arg * arg2;
        if ((abs(arg) | abs(arg2)) >>> 31 == 0L || (arg2 == 0L || multiplied / arg2 == arg) && (arg != -9223372036854775808L || arg2 != -1L)) {
            return multiplied;
        } else {
            throw new ArithmeticException("long overflow");
        }
    }

    public static int incrementExact(int arg) {
        if (arg + 1 < arg) {
            throw new ArithmeticException("integer overflow");
        } else {
            return arg + 1;
        }
    }

    public static long incrementExact(long arg) {
        if (arg + 1L < arg) {
            throw new ArithmeticException("long overflow");
        } else {
            return arg + 1L;
        }
    }

    public static int decrementExact(int arg) {
        if (arg - 1 > arg) {
            throw new ArithmeticException("integer overflow");
        } else {
            return arg - 1;
        }
    }

    public static long decrementExact(long arg) {
        if (arg - 1L > arg) {
            throw new ArithmeticException("long overflow");
        } else {
            return arg - 1L;
        }
    }

    public static int negateExact(int arg) {
        if (arg == -arg && arg != 0) {
            throw new ArithmeticException("integer overflow");
        } else {
            return -arg;
        }
    }

    public static long negateExact(long arg) {
        if (arg == -arg && arg != 0) {
            throw new ArithmeticException("long overflow");
        } else {
            return -arg;
        }
    }

    public static int toIntExact(long arg) {
        if ((long) ((int) arg) != arg) {
            throw new ArithmeticException("integer overflow");
        } else {
            return (int) arg;
        }
    }

    public static int floorDiv(int arg, int arg2) {
        int divided = arg / arg2;
        if ((arg ^ arg2) < 0 && divided * arg2 != arg) {
            --divided;
        }

        return divided;
    }

    public static long floorDiv(long arg, long arg2) {
        long divided = arg / arg2;
        if ((arg ^ arg2) < 0L && divided * arg2 != arg) {
            --divided;
        }

        return divided;
    }

    public static int floorMod(int arg, int arg2) {
        return arg - floorDiv(arg, arg2) * arg2;
    }

    public static long floorMod(long arg, long arg2) {
        return arg - floorDiv(arg, arg2) * arg2;
    }

    public static int abs(int arg) {
        return arg < 0 ? -arg : arg;
    }

    public static long abs(long arg) {
        return arg < 0L ? -arg : arg;
    }

    public static float abs(float arg) {
        return arg <= 0.0F ? 0.0F - arg : arg;
    }

    public static double abs(double arg) {
        return arg <= 0.0D ? 0.0D - arg : arg;
    }

    public static int max(int arg, int arg2) {
        return arg >= arg2 ? arg : arg2;
    }

    public static long max(long arg, long arg2) {
        return arg >= arg2 ? arg : arg2;
    }

    public static float max(float arg, float arg2) {
        if (arg != arg) {
            return arg;
        } else {
            return arg >= arg2 ? arg : arg2;
        }
    }

    public static double max(double arg, double arg2) {
        if (arg != arg) {
            return arg;
        } else {
            return arg >= arg2 ? arg : arg2;
        }
    }

    public static int min(int arg, int arg2) {
        return arg <= arg2 ? arg : arg2;
    }

    public static long min(long arg, long arg2) {
        return arg <= arg2 ? arg : arg2;
    }

    public static float min(float arg, float arg2) {
        if (arg != arg) {
            return arg;
        } else {
            return arg <= arg2 ? arg : arg2;
        }
    }

    public static double min(double arg, double arg2) {
        if (arg != arg) {
            return arg;
        } else {
            return arg <= arg2 ? arg : arg2;
        }
    }

    public static double signum(double arg) {
        return arg != 0.0D && !Double.isNaN(arg) ? copySign(1.0D, arg) : arg;
    }

    public static float signum(float arg) {
        return arg != 0.0F && !Float.isNaN(arg) ? copySign(1.0F, arg) : arg;
    }

    public static double sinh(double arg) {
        return (double) getMathFunction("sinh").$apply(object(arg));
    }

    public static double cosh(double arg) {
        return (double) getMathFunction("cosh").$apply(object(arg));
    }

    public static double tanh(double arg) {
        return (double) getMathFunction("tanh").$apply(object(arg));
    }

    public static double hypot(double arg, double arg2) {
        return (double) getMathFunction("hypot").$apply(object(arg), object(arg2));
    }

    public static double expm1(double arg) {
        return (double) getMathFunction("expm1").$apply(object(arg));
    }

    public static double log1p(double arg) {
        return (double) getMathFunction("log1p").$apply(object(arg));
    }

    public static double copySign(double arg, double arg2) {
        if ((arg < 0.0 && arg2 > -0.0) || (arg > -0.0 && arg2 < 0.0))
            arg = -arg;
        return arg;
    }

    public static float copySign(float arg, float arg2) {
        if ((arg < 0.0 && arg2 > -0.0) || (arg > -0.0 && arg2 < 0.0))
            arg = -arg;
        return arg;
    }

    private static final class RandomNumberGeneratorHolder {
        static final Random randomNumberGenerator = new Random();

        private RandomNumberGeneratorHolder() {
        }
    }
}
