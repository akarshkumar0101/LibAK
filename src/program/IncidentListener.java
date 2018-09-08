package program;

@FunctionalInterface
public interface IncidentListener {

    public abstract void incidentReported(Object... specifications);

}