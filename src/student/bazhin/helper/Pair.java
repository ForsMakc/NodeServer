package student.bazhin.helper;

public class Pair<T1,T2> {
    protected T1 first;
    protected T2 second;

    public Pair(T1 first, T2 second) {
        setPair(first,second);
    }

    public Pair<T1,T2> setPair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
        return this;
    }

    public T1 getFirst() {
        return first;
    }

    public T2 getSecond() {
        return second;
    }

}
