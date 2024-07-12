from os.path import dirname
from sqlite_local_config import config

here = dirname(__file__)
run_dir = f"{here}/run"

config.update(
    {
		"db_url": "postgres://root:root@localhost:5432/rmf_db",
        "cache_directory": f"{run_dir}/cache",  # The directory where cached files should be stored.
    }
)
