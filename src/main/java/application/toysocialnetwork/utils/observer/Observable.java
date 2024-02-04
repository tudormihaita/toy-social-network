package application.toysocialnetwork.utils.observer;

import application.toysocialnetwork.utils.events.NetworkEvent;

public interface Observable<E extends NetworkEvent> {
    void addObserver(Observer<E> o);
    void removeObserver(Observer<E> o);
    void notify(E t);
}
