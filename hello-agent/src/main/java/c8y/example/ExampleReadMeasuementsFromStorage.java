package c8y.example;

import c8y.IsDevice;
import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectReferenceCollectionRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.inventory.ManagedObjectCollection;
import com.cumulocity.sdk.client.inventory.PagedManagedObjectCollectionRepresentation;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import com.cumulocity.sdk.client.measurement.MeasurementCollection;
import com.cumulocity.sdk.client.measurement.MeasurementFilter;
import com.cumulocity.sdk.client.measurement.PagedMeasurementCollectionRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class ExampleReadMeasuementsFromStorage {
    private static final Logger log = LoggerFactory.getLogger(ExampleReadMeasuementsFromStorage.class);

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");

    public static void main(String[] args) {
        Platform platform = new PlatformImpl("https://dtag.cumulocity.com", new CumulocityCredentials("pbreucki", "PB1234$"));
        InventoryApi inventory = platform.getInventoryApi();
        ManagedObjectCollection mos = inventory.getManagedObjects();
        PagedManagedObjectCollectionRepresentation mos2 = mos.get(6);
        for (ManagedObjectRepresentation mo : mos2) {
            System.out.println(mo.getName());
        }

        MeasurementApi measurementApi = platform.getMeasurementApi();
        MeasurementFilter filter = new MeasurementFilter();
        filter.bySource(GId.asGId(44802000));
        //filter.byType("de_patrickbreucking_i2cagent_cumulocity_TemperatureSensor");


        MeasurementCollection measurements = measurementApi.getMeasurementsByFilter(filter);
        PagedMeasurementCollectionRepresentation result = measurements.get(2000);

        FileOutputStream fop = null;
        File file;


        try {
            String dirString = System.getProperty("user.home");
            file = new File(dirString + "/tmp/measurements-temp.json");
            //  for (MeasurementRepresentation mr : result.allPages()) {

            if (file.exists()) {
                file.delete();
            }

            file.createNewFile();
            fop = new FileOutputStream(file);

            Iterator<MeasurementRepresentation> iter = result.allPages().iterator();

            long counter = 0;

            fop.write('[');

            while (iter.hasNext()) {

                //System.out.println(mr.toJSON());
                if (counter % 2000 == 0) {
                    log.info("(" + sdf.format(new Date()) + ")Loading Counter: " + counter);
                }

                if (!iter.hasNext()) {
                    fop.write(']');
                    break;
                }


                fop.write((iter.next().toJSON()).getBytes());
                if (iter.hasNext()) {
                    fop.write(',');
                }

                counter++;
            }


            fop.flush();
            fop.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            try {
                if (fop != null) {
                    fop.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        System.out.println("Ready!");


    }
}
