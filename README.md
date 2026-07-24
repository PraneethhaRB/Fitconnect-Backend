# FitConnect

A full-stack fitness community platform — think Strava's social layer, but organized around personal health goals rather than logged activities. Users join goal-based communities (weight loss, muscle building, healthy lifestyle), get admin-approved into them, share progress photos in real-time group chat, and see nearby diagnostic lab offers tied to their fitness journey.

## Why this is different from a typical fitness tracker
Most fitness apps assume you're already active and want to log/share workouts. FitConnect assumes the opposite: someone hasn't started, or is struggling, and needs an accountability circle plus real diagnostic checkpoints. The core loop is "post your progress, get approved into a curated support group, track real health data" — not "log a run, get kudos."

## Tech Stack
**Backend:** Java 17, Spring Boot 3, Spring Security, Spring Data JPA, MySQL, JWT (JJWT), WebSocket (STOMP)
**Frontend:** React (Vite), Tailwind CSS, React Router, Axios, STOMP.js + SockJS

## Features
- JWT-based authentication (signup/login, BCrypt password hashing, stateless sessions)
- Goal-based community discovery and recommendation
- Two-step community membership: request → admin approval/rejection
- Per-community authorization (admins can only manage their own community)
- Real-time group chat via WebSocket (STOMP over SockJS)
- Image upload in chat with a swappable storage abstraction (local disk now, S3-ready interface)
- Dashboard aggregating profile, communities, and nearby lab partner offers

## Architecture Highlights
- **Stateless JWT auth**: identity is extracted from the verified token via `SecurityContextHolder`, never trusted from client-supplied IDs
- **Hybrid REST + WebSocket chat**: sending a message is a normal authenticated REST call (validation/authorization happen here); after persisting, the server broadcasts it over WebSocket so it's never duplicated logic between read and write paths
- **WebSocket subscription security**: a custom STOMP channel interceptor verifies JWT + community membership on every `SUBSCRIBE` frame, so non-members can't listen in by connecting directly to the socket
- **Storage abstraction**: `StorageService` interface with a local-disk implementation today; swapping to S3 later requires no controller/service changes, just a new implementation class

## Running locally

### Backend
```bash
cd fitconnect-backend
# Create a MySQL database named `fitconnect`
# Update src/main/resources/application.properties with your DB credentials and a JWT secret (openssl rand -base64 32)
mvn spring-boot:run
```
Runs on `http://localhost:8080`

### Frontend
```bash
cd fitconnect-frontend
npm install
npm run dev
```
Runs on `http://localhost:5173`

## API Overview
| Endpoint | Description |
|---|---|
| `POST /auth/signup` | Create account, returns JWT + profile |
| `POST /auth/login` | Authenticate, returns JWT + profile |
| `GET /dashboard` | Aggregated user dashboard |
| `PUT /dashboard/goal` | Update personal goal |
| `POST /communities/{id}/join` | Request to join a community |
| `GET /communities/{id}/pending` | Admin: list pending requests |
| `POST /communities/{id}/approve/{membershipId}` | Admin: approve a request |
| `POST /communities/{id}/reject/{membershipId}` | Admin: reject a request |
| `GET /communities/{id}/messages` | Paginated chat history |
| `POST /communities/{id}/messages` | Send message (text/image, multipart) |
| WebSocket `ws://localhost:8080/ws` | Subscribe to `/topic/community/{id}` for live messages |

## Screenshots
*(add 3-4 screenshots here: login, dashboard, community browse, chat in action — this is genuinely worth doing, visual proof beats reading text)*
