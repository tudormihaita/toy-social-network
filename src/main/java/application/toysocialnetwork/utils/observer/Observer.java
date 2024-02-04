package application.toysocialnetwork.utils.observer;

import application.toysocialnetwork.utils.events.NetworkEvent;

public interface Observer<E extends NetworkEvent> {
    void update(E e);
}
