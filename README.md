# Miniature Spring API

A social network API built with Spring Boot that allows users to create posts, comment, like, and share content within groups.

## Features

### Posts
- [x] Create text posts with attachments
- [x] Comment on posts
- [] Like/unlike posts
- [x] Share posts
- [] Draft posts
- [x] View posts by trending or newest first
- [x] Threaded comments support

### Groups
- [] Create public/private groups
- [] Join groups
- [] Role-based permissions (admin, moderator, editor)
- [x] Share posts within groups

### Attachments
Support for multiple attachment types:
- [x] Links
- [x] Images
- [x] Videos  
- [x] Documents
- [x] Post shares

### Social Features
- [] Follow other users
- [] User notification settings

## Technical Stack

- Spring Boot 3.x
- Spring Security
- Spring Data JPA
- PostgreSQL
- Gradle

## Database Schema

The application uses PostgreSQL with the following main entities:

- User - User accounts and authentication
- Post - Content posts with attachments
- Group - User groups with roles
- Attachment - Media and content attachments 
- Settings - User preferences
- Like - Post likes tracking
- Follow - User following relationships

## API Endpoints

### Posts
- `GET /api/posts` - List all posts
- `GET /api/posts/trending` - Get trending posts
- `GET /api/posts/newest` - Get newest posts
- `GET /api/posts/{id}` - Get post with comments
- `POST /api/posts` - Create new post
- `POST /api/posts/{id}/comments` - Comment on post

### Groups 
- Group management endpoints (TBD)

### Authentication
- `GET /api/auth/login` - Retrieve user token using `username` and `password`
- `GET /api/auth/register` - Register a new user with `username` and `password`

## Getting Started

1. Clone the repository
2. Configure PostgreSQL connection in `application.yml` (If you have docker, you can run `docker compose up -d db`, migrations are pushed automatically at startup)
3. Run: `./gradlew bootRun`

## React client

React client need to be developped and served using `npm start`.

You can see the mockup of the React client in the `docs/` directory.

You can also import the `Bruno` workspace from the `docs/` directory which document the API usage and allow you to test it without any clients.

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License

[MIT](https://choosealicense.com/licenses/mit/)
