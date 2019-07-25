/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package effects.simple;

import javafx.concurrent.Task;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import output.Output;

/**
 *
 * @author 'Aaron Lomba'
 */
public class EffectManager {
    
    private static Thread th;
    private static Task tsk;
    private static boolean initialized = false;
    private static List<Effect> effects = new ArrayList<>();
    private static Lock lock = new ReentrantLock(true);
    
    public static void init() {
        if (initialized) {
            throw new IllegalStateException("The manager has already been initialized!");
        }
        
        
        tsk = new Task() {
            @Override
            protected Object call() throws Exception {
                while (!isCancelled()) {
                    lock.lock();
                    try {
                        for (Effect e : effects) {
                            if(e.getOutput().isActive() && e.getOutput().getPort().isOpened())
                                e.doShit();
                        }
                    } catch (Exception e) {
                    } finally {
                        lock.unlock();
                        
                    }
                    Thread.sleep(100);
                }
                return null;
            }
            
        };
        
        th = new Thread(tsk);
        th.start();
        System.out.println("The effects manager has been initialized!");
        initialized = true;
    }
    
    public static Effect getMyEffect(Output o){
        for (Effect e : effects){
            if (e.getOutput() == o)
                return e;
        }
       return null;
    }
    
    public static void stop() {
        if (!initialized) {
            throw new IllegalStateException("The manager has not been initialized!");
        }
        tsk.cancel();
        initialized = false;
    }
    
    public static void interrupt() {
        if (!initialized) {
            throw new IllegalStateException("The manager has not been initialized!");
        }
        tsk.cancel(true);
        th.interrupt();
        initialized = false;
    }
    
    public static Effect removeEffect(Effect e) {
        if (!initialized) {
            throw new IllegalStateException("The manager has not been initialized!");
        }
        return effects.remove(0);
    }
    
    public static Effect removeEffect(int i) {
        if (!initialized) {
            throw new IllegalStateException("The manager has not been initialized!");
        }
        return effects.remove(i);
    }
    
    public static void submitEffect(Effect e) {
        if (!initialized) {
            throw new IllegalStateException("The manager has not been initialized!");
        }
        System.out.println("acquiring lock to submit effect...");
        lock.lock();
        System.out.println("lock acquired!");
        try {
            if (!effects.contains(e)) {
                Effect map = alreadyMapped(e);
                if(map == null){
                    effects.add(e);
                }else{
                    effects.remove(map);
                    effects.add(e);
                }
            }
        } finally {
            System.out.println("releasing lock");
            lock.unlock();
        }
        
    }
    
    private static Effect alreadyMapped(Effect x){
        for(Effect e : effects){
            if(x.getOutput().equals(e.getOutput())){
                return e;
            }
        }
        return null;
    }
    
}
