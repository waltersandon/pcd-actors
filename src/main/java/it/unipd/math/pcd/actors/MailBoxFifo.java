package it.unipd.math.pcd.actors;

import java.util.concurrent.LinkedBlockingQueue;


/**
 * Created by waltersandon on 05/02/16.
 */
public class MailBoxFifo<T extends Message> implements MailBox<T>{

    private final LinkedBlockingQueue<MessageContent> queue;

    public MailBoxFifo(){

        queue = new LinkedBlockingQueue<MessageContent>();
    }

    /**
     *
     * Return (and remove) the first Packet in the queue.
     * @return Packet or Null if the queue is empty
     */

    @Override
    public MessageContent<? extends Message> pop(){
        try {
            return queue.take();
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void put(T message, ActorRef<? extends Message> sender){
        try {
            queue.put(new MessageContent<T>(message, sender));
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    @Override
    public int size(){
        return queue.size();
    }

    @Override
    public void remove() {
        queue.remove();
    }

}
