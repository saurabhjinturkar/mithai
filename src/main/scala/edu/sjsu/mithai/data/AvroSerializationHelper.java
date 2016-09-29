package edu.sjsu.mithai.data;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;

public class AvroSerializationHelper {

    private Schema schema;

    public void loadSchema(String schemaFile) throws IOException {
        Schema.Parser parser = new Schema.Parser();
        URL url = getClass().getClassLoader().getResource(schemaFile);
        this.schema = parser.parse(new File(url.getFile()));
        System.out.println(schema.getFields());
    }

    public String serialize(GenericRecord data) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        DatumWriter<GenericRecord> writer = new SpecificDatumWriter<GenericRecord>(schema);

        writer.write(data, encoder);
        encoder.flush();
        out.close();
        byte[] serializedBytes = out.toByteArray();

        return new String(Base64.getEncoder().encode(serializedBytes));
//
//
//
//        // Serialize user1 and user2 to disk
//        File file = new File("users.avro");
//        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<GenericRecord>(schema);
//        DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<GenericRecord>(datumWriter);
//        dataFileWriter.create(schema, file);
//        dataFileWriter.append(user1);
//        dataFileWriter.append(user2);
//        dataFileWriter.close();
//
//        // Deserialize users from disk
//        DatumReader<GenericRecord> datumReader = new GenericDatumReader<GenericRecord>(schema);
//        DataFileReader<GenericRecord> dataFileReader = new DataFileReader<GenericRecord>(file, datumReader);
//
//        user = null;
//        while (dataFileReader.hasNext()) {
//            // Reuse user object by passing it to next(). This saves us from
//            // allocating and garbage collecting many objects for files with
//            // many items.
//            user = dataFileReader.next(user);
//            System.out.println(user);
//        }
    }

    public GenericRecord deserialize(String data) throws IOException {

        byte[] bytes = Base64.getDecoder().decode(data.getBytes());

        SpecificDatumReader<GenericRecord> reader = new SpecificDatumReader<GenericRecord>(schema);
        Decoder decoder = DecoderFactory.get().binaryDecoder(bytes, null);
        GenericRecord record = reader.read(null, decoder);
        return record;
    }

    public Schema getSchema() {
        return schema;
    }
}