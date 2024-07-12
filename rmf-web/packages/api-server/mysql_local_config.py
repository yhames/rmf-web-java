from os.path import dirname
from sqlite_local_config import config

here = dirname(__file__)
run_dir = f"{here}/run"

config.update(
    {
        "db_url": "mysql://root:root@127.0.0.1:3306/rmf_db", # TODO: password should be in a secret
        "cache_directory": f"{run_dir}/cache",  # The directory where cached files should be stored.
    }
)
