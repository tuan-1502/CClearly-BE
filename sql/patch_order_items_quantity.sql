DECLARE @objectName NVARCHAR(256);
DECLARE @quotedTable NVARCHAR(300);

SELECT TOP 1
    @objectName = SCHEMA_NAME(schema_id) + '.' + name,
    @quotedTable = QUOTENAME(SCHEMA_NAME(schema_id)) + '.' + QUOTENAME(name)
FROM sys.tables
WHERE LOWER(name) = LOWER('Order_Items')
  AND SCHEMA_NAME(schema_id) = 'dbo';

IF @objectName IS NULL
BEGIN
    THROW 50000, 'Order_Items table was not found in dbo schema.', 1;
END;

IF COL_LENGTH(@objectName, 'quantity') IS NULL
BEGIN
    EXEC('ALTER TABLE ' + @quotedTable + ' ADD [quantity] INT NULL;');
END;

EXEC('UPDATE ' + @quotedTable + ' SET [quantity] = 1 WHERE [quantity] IS NULL;');
