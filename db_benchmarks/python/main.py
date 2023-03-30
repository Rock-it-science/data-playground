import pandas as pd
from pymongo import MongoClient
from sqlalchemy import create_engine, text
from timeit import default_timer as timer
from tqdm import tqdm

tqdm.pandas()

def load_data(nrows: int):
    print(f'Loading {nrows} rows of data')
    return pd.read_csv('data/vehicles.csv', nrows=nrows)


def postgres(df: pd.DataFrame):
    print('\nPostgres')
    engine = create_engine('postgresql://postgres:postgres@localhost:1001/pg')
    start = timer()
    df.to_sql('vehicles', engine, if_exists='replace')
    end = timer()
    print(f'Load time {round(end - start, 2)}s')

    with engine.connect() as conn:
        start = timer()
        result = conn.execute(text("SELECT * FROM vehicles"))
        r = result.all()
        end = timer()
        print(f'Query time time {round(end - start, 2)}s')


def mysql(df: pd.DataFrame):
    print('\nMySQL')
    engine = create_engine('mysql+pymysql://root:pass@localhost:1002/mysql')
    start = timer()
    df.to_sql('vehicles', engine, if_exists='replace')
    end = timer()
    print(f'Load time {round(end - start, 2)}s')

    with engine.connect() as conn:
        start = timer()
        result = conn.execute(text("SELECT * FROM vehicles"))
        r = result.all()
        end = timer()
        print(f'Query time time {round(end - start, 2)}s')


def mongo(df: pd.DataFrame):
    print('\nMongo')
    client = MongoClient('mongodb://root:pass@localhost:1003/')
    db = client.mongo
    collection = db.vehicles
    
    start = timer()
    collection.insert_many(df.to_dict('records'))
    end = timer()
    print(f'Load time {round(end - start, 2)}s')

    start = timer()
    result = collection.find({})
    end = timer()
    print(f'Query time time {round(end - start, 2)}s')



if __name__ == "__main__":
    df = load_data(10000)
    # postgres(df)
    # mysql(df)
    mongo(df)
