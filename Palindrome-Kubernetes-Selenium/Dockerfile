# Multi-stage lightweight Node.js container for Kubernetes Deployment
FROM node:20-alpine AS base

WORKDIR /usr/src/app

# Install dependencies
COPY app/package.json ./
# If package-lock.json exists, npm ci, else npm install --production
RUN npm install --production --no-audit --no-fund

# Bundle application source
COPY app/ ./

# Expose port 3000
EXPOSE 3000

# Set non-root user for security compliance in Kubernetes
USER node

# Healthcheck configuration
HEALTHCHECK --interval=15s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:3000/health || exit 1

# Start server
CMD ["node", "server.js"]
