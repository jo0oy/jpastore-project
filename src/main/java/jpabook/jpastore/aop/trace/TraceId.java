package jpabook.jpastore.aop.trace;

import lombok.Getter;

import java.util.UUID;

@Getter
public class TraceId {

    private String id;
    private int level;

    public TraceId() {
        this.id = createId();
        this.level = 0;
    }

    public TraceId(String id, int level) {
        this.id = id;
        this.level = level;
    }

    public TraceId nextId() {
        return new TraceId(this.id, level + 1);
    }

    public TraceId previousId() {
        return new TraceId(this.id, level - 1);
    }

    public boolean isFirstLevel() {
        return this.level == 0;
    }

    private String createId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
