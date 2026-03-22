FROM node:20-alpine AS build-stage
WORKDIR /vue3-app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

FROM nginx:stable-alpine AS production-stage

COPY --from=build-stage /vue3-app/dist /usr/share/nginx/html

COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 88

CMD ["nginx", "-g", "daemon off;"]