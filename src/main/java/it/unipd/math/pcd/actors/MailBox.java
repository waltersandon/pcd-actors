package it.unipd.math.pcd.actors;

/**
 * Created by waltersandon on 05/02/16.
 */
public interface MailBox<T extends Message> {

    public MessageContent<? extends Message> pop();
    public void put(T message, ActorRef<? extends Message> sender);
    public int size();
    public void remove();

}
