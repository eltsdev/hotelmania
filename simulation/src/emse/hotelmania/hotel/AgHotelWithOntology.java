package emse.hotelmania.hotel;

import jade.content.onto.Ontology;
import jade.core.Agent;
import emse.hotelmania.ontology.HotelmaniaOntology;

public class AgHotelWithOntology extends Agent 
{
	private static final long serialVersionUID = 2893904717857535232L;
	
	@Override
	protected void setup() {

		System.out.println(getLocalName()+": HAS ENTERED");

		Ontology ontology = HotelmaniaOntology.getInstance();
		for (Object p : ontology.getPredicateNames()) {
			System.out.println("My predicate is: "+p);
		}

	}
}
