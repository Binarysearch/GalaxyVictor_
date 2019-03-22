package com.galaxyvictor.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class FutureEventManager {

    private final PriorityQueue<FutureEvent> events = new PriorityQueue<FutureEvent>(new Comparator<FutureEvent>() {

        public int compare(FutureEvent f1, FutureEvent f2) {
            return (int) (f1.getEndTime() - f2.getEndTime());
        }

    });

    private final Thread thread = new Thread(){
        public void run(){
            
            while (true) {
                try {
                    FutureEvent ev = null;
                    synchronized(events){
                        ev = events.peek();
                    }
                    if(ev != null){
                        double tiempo = ev.getRemainingTime();
                        
                        if(tiempo <= 0){
                            synchronized(events){
                                events.poll();
                            }
                            ev.finish();
                        }else{
                            try {
                                Thread.sleep((long) tiempo);
                            } catch (InterruptedException e) {
                                
                            }
                        }
                    }else{
                        Thread.sleep(10000);
                    }
                } catch (InterruptedException e) {
                    
                }
                
            }
        }
    };

    public synchronized void start(){
        if(thread.isAlive()){
            return;
        }
        thread.start();
    }

	public void addFutureEvent(FutureEvent ev) {
        synchronized(events){
            events.add(ev);
            thread.interrupt();
        }
	}

	public List<FutureEvent> getEvents() {
        synchronized(events){
            return new ArrayList<>(events);
        }
	}
}