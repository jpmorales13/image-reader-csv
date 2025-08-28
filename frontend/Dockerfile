# Dockerfile with custom Nginx config
FROM nginx:alpine

# Copy frontend files and Nginx config
COPY . /usr/share/nginx/html

EXPOSE 5173
CMD ["nginx", "-g", "daemon off;"]
