package car.tp2;

import java.util.List;

public class ConfigMock extends Config {

	protected void addResources( List<Object> resources ) {
		resources.add( new FtpResource() );
		// resources.add( new MaClasseDeResource() );
	}
}
