# StorageMan Frontend

A modern React + TypeScript frontend for the StorageMan cloud storage management application.

## Tech Stack

- **React 18** - UI framework
- **TypeScript** - Type safety
- **Vite** - Build tool and dev server
- **Tailwind CSS** - Utility-first CSS framework
- **TanStack Query (React Query)** - Server state management
- **Axios** - HTTP client
- **Orval** - OpenAPI client generator

## Prerequisites

- Node.js 18+ and pnpm
- Backend API running on `http://localhost:8080`

## Setup

1. Install dependencies:
```bash
pnpm install
```

2. Copy environment file:
```bash
cp .env.example .env
```

3. Update `.env` with your configuration if needed:
```env
VITE_API_BASE_URL=http://localhost:8080
```

## Development

### Start Development Server

```bash
pnpm dev
```

The app will be available at `http://localhost:5173`

### Generate API Client

Before generating the API client, ensure the backend is running and the OpenAPI spec is available at `http://localhost:8080/v3/api-docs`.

To generate the TypeScript API client from the backend OpenAPI specification:

```bash
pnpm generate:api
```

This will:
- Fetch the OpenAPI spec from the backend
- Generate TypeScript types and React Query hooks
- Output generated code to `src/api/generated/`

**Note:** The generated files are gitignored. You need to run this command after:
- Cloning the repository
- Backend API changes
- Switching branches with API changes

### Other Scripts

```bash
pnpm build       # Build for production
pnpm preview     # Preview production build
pnpm lint        # Run ESLint
```

## Project Structure

```
front/
├── src/
│   ├── api/
│   │   ├── axios-instance.ts    # Axios configuration with interceptors
│   │   ├── query-client.ts      # React Query client setup
│   │   └── generated/           # Auto-generated API client (gitignored)
│   ├── App.tsx                  # Main app component
│   ├── main.tsx                 # App entry point
│   └── index.css                # Tailwind directives
├── orval.config.ts              # Orval configuration
├── tailwind.config.js           # Tailwind configuration
├── vite.config.ts               # Vite configuration
└── package.json
```

## API Client Usage

After generating the API client, you can use the generated React Query hooks:

```typescript
import { useGetUsers, useCreateUser } from '@/api/generated/users';

function UsersList() {
  // Auto-generated hook with TypeScript types
  const { data, isLoading, error } = useGetUsers();

  const createUserMutation = useCreateUser();

  const handleCreate = () => {
    createUserMutation.mutate({
      name: 'John Doe',
      email: 'john@example.com'
    });
  };

  // ...
}
```

## Authentication

The axios instance is configured with an interceptor that automatically:
- Adds the JWT token from localStorage to requests
- Redirects to `/login` on 401 responses
- Clears the auth token on unauthorized access

## Configuration

### Orval

The `orval.config.ts` file configures how the API client is generated:
- **Input**: OpenAPI spec URL
- **Output**: Generated files location and format
- **Client**: React Query hooks
- **Mutator**: Custom axios instance with auth

### Tailwind CSS

Configured in `tailwind.config.js` with content paths for all TSX files.

### TypeScript

Path aliases are configured:
- `@/*` maps to `./src/*`

Available in both TypeScript (`tsconfig.json`) and Vite (`vite.config.ts`).

## Troubleshooting

### API Generation Fails

Ensure:
1. Backend is running on port 8080
2. OpenAPI endpoint is accessible: `http://localhost:8080/v3/api-docs`
3. SpringDoc dependency is added to backend `pom.xml`

### CORS Errors

Configure CORS in your Spring Boot backend to allow `http://localhost:5173`.
