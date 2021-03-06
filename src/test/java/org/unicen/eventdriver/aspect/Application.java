package org.unicen.eventdriver.aspect;

import java.util.Collections;
import java.util.List;

import org.unicen.aspect.Aspect;
import org.unicen.aspect.AspectFactory;
import org.unicen.eventdriver.EventHandler;
import org.unicen.eventdriver.EventListener;
import org.unicen.eventdriver.EventProviderFactory;
import org.unicen.eventdriver.MapEventHandler;
import org.unicen.operation.OperationContext;
import org.unicen.operation.SimpleOperation;
import org.unicen.operation.event.OnFailEvent;
import org.unicen.operation.event.OnFinishEvent;
import org.unicen.operation.event.OnStartEvent;
import org.unicen.operation.event.StartFinishListener;
import org.unicen.operation.support.SimpleOperationAspectSupport;
import org.unicen.operation.test.TestPlan;

public class Application {

	public static void main(String[] args) throws Throwable {

//		Operacion WebService: que en realidad es HttpOperation (ojo que en Android hay otros clientes)
//      Authorization managers -> basic, o generar 1 por operacion/hilo
//      Ejecucion por linea de comandos (agregar variables de entorno)
//	    Ejecucion por ADB ...

//      Ir preparando pre processors: para CSV, datasets (1 hilo/operacion por linea)
//      Ir preparando post processors: para DB, web, Extractores de variables (regexp)
//		Assertions -> red/green (condiciones por operador... o programaticas)
//	    Bifurcadores de secuencia (ForkOperation)
	    
//	    Donde estan las Metricas? Que es una metrica
//      Variables de contexto en Jmeter
//      Estudiar tambien SOAP ui
		
		EventHandler handler = new MapEventHandler();
		EventProviderFactory providerFactory = new EventProviderFactory(handler);

		SimpleOperation testPlan = providerFactory.resolveProviders(new TestPlan());

		EventListener listener = new StartFinishListener() {
            
            @Override
            public void onStart(OnStartEvent onStartEvent) {
             
                System.out.println("starting");
            }
            
            @Override
            public void onFinish(OnFinishEvent onFinishEvent) {

                System.out.println("finishing");
            }
            
            @Override
            public void onFail(OnFailEvent onFailEvent) {
                System.out.println("failing");
            }
        };
		handler.subscribeListener(testPlan, listener);

		Aspect<SimpleOperation> aspect = new SimpleOperationAspectSupport() {
			
			@Override
			public void beforeExecute(SimpleOperation operation, OperationContext context) {
				
				System.out.println("Holaaa aspectoo");
			}
			
			@Override
            public void afterExecute(SimpleOperation operation, Object result, Exception exception, OperationContext context) {
                
                System.out.println("Chau aspectoo");
            }
			
		};
		
		List<Aspect<SimpleOperation>> aspects = Collections.singletonList(aspect);
		SimpleOperation apectTestPlan  = new AspectFactory().getWrapperInstance(testPlan, aspects);
		
		apectTestPlan.execute(new OperationContext());
	}

}
