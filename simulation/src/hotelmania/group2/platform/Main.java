package hotelmania.group2.platform;

import jade.Boot;

public class Main {

	public static void main(String[] args) {
		
		String agents = "hotelmania:hotelmania.group2.platform.AgHotelmania" + ";"
						+ "hotel2:hotelmania.group2.hotel.AgHotel2" + ";"
						+ "client1:hotelmania.group2.platform.AgClient" + ";";
		String[] param = {
				"-gui",		
				"--local-host",
				"-127.0.0.2",
				agents
		};
		Boot.main( param );
		
		
		/*RunJade r=new RunJade(true,"30000");// run a main container on port 30000 on current machine
		// the RunJade java class can be found in the example (click this for the code)
		// remember to include jade.jar
		//NOTE: running Jade is not the challenge but creating agents (for the moment there is no need to detail 
		//what happens in the class RunJade)
				
		//you will need this pointer to the created container in order to be able to create agents
		ContainerController home = r.getHome();
				

		AgHotelmania hotelMania = */

	}
}
