import React from 'react' ;

interface SearchBarProps{
    query: string;
    onChange: (value: string)=>void;
}

const SearchBarComponent : React.FC<SearchBarProps> = ({ query, onChange}) =>{
    return (
        <div className="mb-4 w-100 d-flex mt-2 ">
            <input
                type="text"
                value={query}
                onChange={e => onChange(e.target.value)}
                placeholder="Search by name or email"
                className="form-control rounded shadow-sm"
                style={{
                    maxWidth: '600px',
                    marginTop: '10px',
                    padding: '10px 16px',
                    fontSize: '1rem',
                    border: '1px solid #ddd',
                    boxShadow: '0 2px 6px rgba(0,0,0,0.05)'
                }}
            />
        </div>
    )
}

export default SearchBarComponent