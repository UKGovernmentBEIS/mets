const axios = require('axios');
const yaml = require('js-yaml');
const fs = require('fs');
const path = require('path');

(async () => {
  try {
    const json = await axios.get('http://localhost:8080/api/v3/api-docs');
    const yml = yaml.dump(json.data);
    const swaggerFile = path.join(__dirname, 'projects', 'pmrv-api', 'src', 'assets', 'swagger.yaml');
    fs.writeFileSync(swaggerFile, yml);
  } catch (e) {
    console.error(e);
    console.error('COULD NOT UPDATE SWAGGER FILE - CHECK THAT API IS UP AND RUNNING');
    process.exit(-1);
  }
})();
