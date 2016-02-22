/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2015 Riccardo Cardin
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * <p/>
 * Please, insert description here.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */

/**
 * Please, insert description here.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */
package it.unipd.math.pcd.actors;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Defines common properties of all actors.
 *
 * @author Walter Sandon
 * @version 1.1
 * @since 1.1
 */
public abstract class AbsActor<T extends Message> implements Actor<T>, Runnable {


    //mailbox where messages will be send
    private final MailBox mailBox = new MailBoxFifo();

    //Self-reference of the actor

    protected ActorRef<T> self;

    //Sender of the current message
    protected ActorRef<? extends Message> sender;

    //Flag for interruption.
    private AtomicBoolean isInterrupted = new AtomicBoolean(false);

    public boolean getFlag(){
        return isInterrupted.get();
    }


    @Override
    public void run() {

        while (!Thread.currentThread().isInterrupted()) {
            //I take the messagge into the mailbox
            MessageContent<T> toProcess = mailBox.pop();
            /*
            By synchronizing the mail box, check that has more messages
            and that it isn't already stopped.
            If it isn't, we set the sender and the message
             */
            synchronized (mailBox) {
                while (mailBox.size() > 0) {
                    if (!isInterrupted.get()) try {
                        mailBox.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    else return;//the mailBox is empty
                }
                //da vedere se è il caso di togliere il messaggio dalla mailBox
            }
            setSender((ActorRef<T>) toProcess.getActorRef());
            receive(toProcess.getMessage());
        }


    }


    protected final void setSender(ActorRef<T> sender) {

        this.sender = sender;
    }

    protected final Actor<T> setSelf(ActorRef<T> self) {

        this.self = self;
        return this;
    }


    //set the flag for interruption at true only if it isn't at now

    protected final void interrupt() {

        isInterrupted.set(true);//is atomic
        synchronized(mailBox) {
            mailBox.notifyAll();
        }
    }

    // Insert into mail box only if the actor is not interrupted

    public final void insertMessage(T message, ActorRef<T> sender) {

        synchronized (mailBox) {
            if (!isInterrupted.get()) {
                mailBox.put(message,sender);
                mailBox.notifyAll();
            }
        }
    }
}
