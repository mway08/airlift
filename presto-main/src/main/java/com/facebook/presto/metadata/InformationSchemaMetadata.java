package com.facebook.presto.metadata;

import com.facebook.presto.spi.ColumnMetadata;
import com.facebook.presto.tuple.TupleInfo;
import com.google.common.collect.ImmutableMap;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static com.facebook.presto.metadata.MetadataUtil.ColumnMetadataListBuilder.columnsBuilder;
import static com.facebook.presto.metadata.MetadataUtil.getType;
import static com.facebook.presto.spi.ColumnType.LONG;
import static com.facebook.presto.spi.ColumnType.STRING;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Iterables.transform;

public class InformationSchemaMetadata
        extends AbstractInformationSchemaMetadata
{
    public static final String INFORMATION_SCHEMA = "information_schema";

    public static final String TABLE_COLUMNS = "columns";
    public static final String TABLE_TABLES = "tables";
    public static final String TABLE_INTERNAL_FUNCTIONS = "__internal_functions__";
    public static final String TABLE_INTERNAL_PARTITIONS = "__internal_partitions__";

    private static final Map<String, List<ColumnMetadata>> METADATA = ImmutableMap.<String, List<ColumnMetadata>>builder()
            .put(TABLE_COLUMNS, columnsBuilder()
                    .column("table_catalog", STRING)
                    .column("table_schema", STRING)
                    .column("table_name", STRING)
                    .column("column_name", STRING)
                    .column("ordinal_position", LONG)
                    .column("column_default", STRING)
                    .column("is_nullable", STRING)
                    .column("data_type", STRING)
                    .build())
            .put(TABLE_TABLES, columnsBuilder()
                    .column("table_catalog", STRING)
                    .column("table_schema", STRING)
                    .column("table_name", STRING)
                    .column("table_type", STRING)
                    .build())
            .put(TABLE_INTERNAL_FUNCTIONS, columnsBuilder()
                    .column("function_name", STRING)
                    .column("argument_types", STRING)
                    .column("return_type", STRING)
                    .build())
            .put(TABLE_INTERNAL_PARTITIONS, columnsBuilder()
                    .column("table_catalog", STRING)
                    .column("table_schema", STRING)
                    .column("table_name", STRING)
                    .column("partition_number", LONG)
                    .column("partition_key", STRING)
                    .column("partition_value", STRING)
                    .build())
            .build();

    @Inject
    public InformationSchemaMetadata()
    {
        super(INFORMATION_SCHEMA, METADATA);
    }

    static TupleInfo informationSchemaTupleInfo(String tableName)
    {
        checkArgument(METADATA.containsKey(tableName), "table does not exist: %s", tableName);
        return new TupleInfo(transform(METADATA.get(tableName), getType()));
    }

    static int informationSchemaColumnIndex(String tableName, String columnName)
    {
        checkArgument(METADATA.containsKey(tableName), "table does not exist: %s", tableName);
        List<ColumnMetadata> columns = METADATA.get(tableName);
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).getName().equals(columnName)) {
                return i;
            }
        }
        throw new IllegalArgumentException("column does not exist: " + columnName);
    }
}