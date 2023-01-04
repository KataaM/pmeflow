#Use latest node image version
FROM redis:6.2-alpine


# Define mountable directories.
VOLUME ["/data"]

# Create app directory
WORKDIR /data

# Define default command.
CMD ["redis-server", "/etc/redis/redis.conf"]

#Expose port on 6379 in the docker container
EXPOSE 6379

# Install app dependencies
# A wildcard is used to ensure both package.json AND package-lock.json are copied
# where available (npm@5+)
#COPY package*.json ./

#RUN npm install
# If you are building your code for production
# RUN npm ci --only=production

# Bundle app source
#COPY . .

#Run npm install
#CMD [ "npm", "install" ]

#Run those commands to launch the app
#CMD [ "npm", "run", "serve" ]
