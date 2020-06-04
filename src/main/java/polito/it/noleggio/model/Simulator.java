package polito.it.noleggio.model;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.PriorityQueue;

import javax.swing.text.StyleConstants.CharacterConstants;

import org.w3c.dom.events.EventException;

import polito.it.noleggio.model.Event.EventType;

public class Simulator {

	//CODA DEGLI EVENTI
	private PriorityQueue<Event> queue=new PriorityQueue<Event>();
	
	//PARAMETRI DI SIMULAZIONE
	private int NC=10; //number of cars
	private Duration T_IN=Duration.of(10, ChronoUnit.MINUTES); //intervallo tra i clienti
	
	private final LocalTime oraApertura=LocalTime.of(8, 00);
	private final LocalTime oraChiusura=LocalTime.of(17, 00);
	
	//MODELLO DEL MONDO
	private int nAutoDisponibili; //auto disponibili nel deposito
	
	//VALORI DA CALCOLARE
	private int clienti;
	private int clientiInsoddisfatti;
	
	//METODI PER RESTITUIRE RISULTATI
	public int getClienti() {
		return clienti;
	}

	public int getClientiInsoddisfatti() {
		return clientiInsoddisfatti;
	}

	//METODI PER IMPOSTARE I PARAMETRI
	public void setNumCars(int N) {
		this.NC=N;
	}
	
	public void setClientFrequency(Duration d) {
		this.T_IN=d;
	}
	
	//SIMULAZIONE VERA E PROPRIA
	public void run() {
		//preparazione iniziale (mondo+coda eventi)
		this.nAutoDisponibili=this.NC;
		this.clienti=this.clientiInsoddisfatti=0;
		
		this.queue.clear();
		LocalTime oraArrivoCliente=this.oraApertura;
		do {
			Event event=new Event(oraArrivoCliente, EventType.NEW_CLIENT);
			queue.add(event);
			oraArrivoCliente=oraArrivoCliente.plus(T_IN);
		} while (oraArrivoCliente.isBefore(oraChiusura)); //alla fine del while avrò la coda piena degli eventi cadenzati ogni T_IN 
		
		//esecuzione vera e propria del ciclo di simulazione
		while(!this.queue.isEmpty()) {
			Event event=this.queue.poll();
			System.out.println(event);
			processEvent(event);
		}
	}
	
	private void processEvent(Event event) {

		switch (event.getType()){
		case NEW_CLIENT:
			
			if(this.nAutoDisponibili>0) {
				//cliente viene servito => aggiorno modello del mondo, aggiorna risultati, genera nuovi eventi
				this.nAutoDisponibili--;
				this.clienti++;
				double num=Math.random(); //restituisce numero compreso tra [0,1)
				Duration travel;
				if(num<1.0/3.0)
					travel=Duration.of(1, ChronoUnit.HOURS);
				else if(num<2.0/3.0)
					travel=Duration.of(2, ChronoUnit.HOURS);
				else 
					travel=Duration.of(3, ChronoUnit.HOURS);
				
				Event nuovo=new Event(event.getTime().plus(travel), EventType.CAR_RETURNED);
				this.queue.add(nuovo);
				
			}else {
				//cliente insoddisfatto
				this.clienti++;
				this.clientiInsoddisfatti++;
			}
			
			break;

		case CAR_RETURNED:
			
			this.nAutoDisponibili++;
			
			break;
		}
	}
	
}
