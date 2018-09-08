package program;

/**
 * An interface to test a if a condition is being met given certain arguments.
 * The casts done inside of this method should take into consideration what will
 * be calling the method and with what arguments.
 * 
 * @author Akarsh
 *
 */
@FunctionalInterface
public interface Condition {

    public static final Condition trueCondition = args -> true;
    public static final Condition falseCondition = args -> false;
    // public static final Condition randomCondition = args -> Math.random() > .5;

    public abstract boolean performCondition(Object... args);

}
