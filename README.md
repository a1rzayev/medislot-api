# medislot-api
Medical reservation app

## Configuration

### Database Configuration

The application requires PostgreSQL database configuration via environment variables:

- `DB_URL` - Database connection URL (default: `jdbc:postgresql://localhost:5432/medislot`)
- `DB_USERNAME` - Database username (default: `postgres`)
- `DB_PASSWORD` - Database password (default: `postgres`)

**Important**: For production environments, always set these environment variables. The default values in `application.properties` are for local development only.

Example:
```bash
export DB_URL=jdbc:postgresql://localhost:5432/medislot
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
```

### Troubleshooting

If the application fails to start with a database connection error:

1. **Ensure PostgreSQL is running:**
   ```bash
   # Check if PostgreSQL is running
   pg_isready
   # Or on macOS with Homebrew:
   brew services list | grep postgresql
   ```

2. **Create the database if it doesn't exist:**
   ```bash
   # Option 1: Create database directly (recommended)
   psql -U postgres -c "CREATE DATABASE medislot;"
   
   # Option 2: Connect interactively
   psql -U postgres
   # Then run: CREATE DATABASE medislot;
   # Exit with: \q
   ```
   
   **Note**: If you get a permission error, you may need to use a different user or grant permissions:
   ```bash
   # Check your PostgreSQL username
   whoami
   
   # Use that username instead
   psql -U $(whoami) -c "CREATE DATABASE medislot;"
   ```

3. **Verify connection settings:**
   - Check that the database name in `DB_URL` matches an existing database
   - Verify username and password are correct
   - Ensure PostgreSQL is listening on the correct port (default: 5432)

4. **Get the full error message:**
   Run Maven with verbose error output:
   ```bash
   cd demo
   ./mvnw spring-boot:run -e 2>&1 | tee error.log
   ```
   
   Look for lines containing:
   - `Connection refused`
   - `FATAL: database "medislot" does not exist`
   - `FATAL: password authentication failed`
   - `Unable to obtain JDBC Connection`
