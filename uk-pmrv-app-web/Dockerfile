FROM nginx:1.30-alpine

RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/Europe/London /etc/localtime \
    && echo "Europe/London" > /etc/timezone

COPY ./.nginx/nginx.conf /etc/nginx/conf.d/default.conf
COPY dist/uk-pmrv-main-web/browser /usr/share/nginx/html/installation-aviation

ENTRYPOINT ["nginx", "-g", "daemon off;"]
